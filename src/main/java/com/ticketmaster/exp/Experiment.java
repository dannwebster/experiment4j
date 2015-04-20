package com.ticketmaster.exp;

import com.ticketmaster.exp.publish.PrintStreamPublisher;
import com.ticketmaster.exp.util.Assert;
import com.ticketmaster.exp.util.Duple;
import com.ticketmaster.exp.util.SameWhens;
import com.ticketmaster.exp.util.Try;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static com.ticketmaster.exp.util.Selectors.ALWAYS;
import static com.ticketmaster.exp.util.Selectors.NEVER;

/**
 * Created by dannwebster on 10/12/14.
 */
public class Experiment<T, M> implements Supplier<Result<T>>, Callable<T> {

    private final String name;
    private final Duple<Supplier<TrialResult<T>>> controlThenCandidate;
    private final BooleanSupplier returnCandidateWhen;
    private final BooleanSupplier doExperimentWhen;
    private final Clock clock;

    private final BiFunction<M, M, Boolean> sameWhen;
    private final BiFunction<Exception, Exception, Boolean> exceptionsSameWhen;
    private final Function<T, M> simplifier;

    private final Publisher<T> publisher;

    public static class Simple<V> extends Experiment<V, V> {
        public Simple(String name,
                      Callable<V> control, Callable<V> candidate,
                      BooleanSupplier returnCandidateWhen,
                      BooleanSupplier doExperimentWhen,
                      Function<V, V> simplifier,
                      BiFunction<V, V, Boolean> sameWhen,
                      BiFunction<Exception, Exception, Boolean> exceptionsSameWhen,
                      Publisher<V> publisher,
                      Clock clock) {
            super(name, control, candidate, returnCandidateWhen,
                    doExperimentWhen, simplifier, sameWhen, exceptionsSameWhen,
                    publisher, clock);
        }
    }

    public static class SimpleBuilder<V> extends BaseBuilder<V, V, Simple<V>, SimpleBuilder<V>> {
        public SimpleBuilder(String name) {
            super(name);
            super.simplifiedBy(a -> a);
        }

        public Simple<V> build() {
            return new Simple<>(name, control, candidate,
                    returnCandidateWhen, doExperimentWhen,
                    simplifier, sameWhen, exceptionsSameWhen,
                    publisher, clock);
        }
    }
    public static class Builder<T, M> extends BaseBuilder<T, M, Experiment<T, M>, Builder<T, M>> {

        public Builder(String name) {
            super(name);
        }

        public Experiment<T, M> build() {
            return new Experiment<>(name, control, candidate,
                    returnCandidateWhen, doExperimentWhen, simplifier,
                    sameWhen, exceptionsSameWhen, publisher, clock);
        }
    }

    public static abstract class BaseBuilder<T, M, E extends Callable<T>, B extends BaseBuilder<T, M, E, B>> {
        String name;
        Callable<T> control;
        Callable<T> candidate;
        BooleanSupplier returnCandidateWhen = NEVER;
        BooleanSupplier doExperimentWhen = ALWAYS;

        Function<T, M> simplifier;
        BiFunction<M, M, Boolean> sameWhen = Objects::equals;
        BiFunction<Exception, Exception, Boolean> exceptionsSameWhen = SameWhens.classesMatch();

        Publisher<T> publisher = PrintStreamPublisher.DEFAULT;

        Clock clock = Clock.systemUTC();

        BaseBuilder(String name) {
            this.name = name;
        }

        public abstract E build();

        private B me() {
            return (B) this;
        }

        public B simplifiedBy(Function<T, M> simplifier) {
            this.simplifier = simplifier;
            return me();
        }

        public B sameWhen(BiFunction<M, M, Boolean> sameWhen) {
            this.sameWhen = sameWhen;
            return me();
        }

        public B exceptionsSameWhen(BiFunction<Exception, Exception, Boolean> sameWhen) {
            this.exceptionsSameWhen = exceptionsSameWhen;
            return me();
        }

        public B timedBy(Clock clock) {
            this.clock = clock;
            return me();
        }

        public B publishedBy(Publisher<T> publisher) {
            this.publisher = publisher;
            return me();
        }

        public B returnCandidateWhen(BooleanSupplier returnCandidateWhen) {
            this.returnCandidateWhen = returnCandidateWhen;
            return me();
        }

        public B doExperimentWhen(BooleanSupplier doExperimentWhen) {
            this.doExperimentWhen = doExperimentWhen;
            return me();
        }

        public B control(Callable<T> control) {
            this.control = control;
            return me();
        }

        public B candidate(Callable<T> candidate) {
            this.candidate = candidate;
            return me();
        }

    }

    public static <T> SimpleBuilder<T> simple(String name) {
        return new SimpleBuilder<>(name);
    }
    public static <T, M> Builder<T, M> named(String name) {
        return new Builder<>(name);
    }

    Experiment(
            String name, 
            Callable<T> control, 
            Callable<T> candidate, 
            BooleanSupplier returnCandidateWhen,
            BooleanSupplier doExperimentWhen,
            Function<T, M> simplifier,
            BiFunction<M, M, Boolean> sameWhen,
            BiFunction<Exception, Exception, Boolean> exceptionsSameWhen,
            Publisher<T> publisher,
            Clock clock) {

        Assert.hasText(name, "name must be non-null and have text");
        Assert.notNull(control, "control must be non-null");
        Assert.notNull(candidate, "candidate must be non-null");
        Assert.notNull(returnCandidateWhen, "returnCandidateWhen must be non-null");
        Assert.notNull(doExperimentWhen, "doExperimentWhen must be non-null");
        Assert.notNull(sameWhen, "sameWhen must be non-null");
        Assert.notNull(exceptionsSameWhen, "exceptionsSameWhen must be non-null");
        Assert.notNull(simplifier, "simplifier must be non-null");
        Assert.notNull(publisher, "publisher must be non-null");
        Assert.notNull(clock, "clock must be non-null");

        this.name = name;
        this.controlThenCandidate = Duple.from(
                () -> observe(CONTROL, control),
                () -> observe(CANDIDATE, candidate)
        );
        this.returnCandidateWhen = returnCandidateWhen;
        this.doExperimentWhen = doExperimentWhen;
        this.sameWhen = sameWhen;
        this.exceptionsSameWhen = exceptionsSameWhen;
        this.simplifier = simplifier;
        this.publisher = publisher;
        this.clock = clock;
    }


    public final T call() throws Exception{
        return perform().call();
    }

    public final Try<T> perform() {
        return (returnCandidateWhen.getAsBoolean() ?
                this.get().getCandidateResult() :
                this.get().getControlResult())
                .getTryResult();
    }

    @Override
    public final Result<T> get() {
        Result<T> result;
        Instant timestamp = Instant.now();
        if (doExperimentWhen.getAsBoolean()) {
            Map<TrialType, List<TrialResult<T>>> results = controlThenCandidate
                    .parallelStream()
                    .map(Supplier::get)
                    .collect(Collectors.groupingBy((TrialResult t) -> t.getTrialType()));

            result = new Result<>(
                    name,
                    timestamp,
                    results.get(CANDIDATE).get(0),
                    results.get(CONTROL).get(0));

            MatchType matchType = determineMatch(result);
            publisher.publish(matchType, result);
        } else {
            TrialResult<T> trialResult = controlThenCandidate.getE1().get();
             result = new Result<>(
                    name,
                    timestamp,
                    trialResult,
                    trialResult);
        }
        return result;
    }

    public MatchType determineMatch(Result<T> result) {
        return result.determineMatch(this.simplifier, this.sameWhen, this.exceptionsSameWhen);
    }

    final TrialResult<T> observe(TrialType trialType, Callable<T> callable) {
        Instant start = clock.instant();
        Exception exception = null;
        T value = null;
        try {
            value = callable.call();
        } catch (Exception t) {
            exception = t;
        }
        Instant end = clock.instant();
        Duration duration = Duration.between(start, end);
        return new TrialResult<>(trialType, duration, exception, value);
    }
}
