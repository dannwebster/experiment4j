package com.ticketmaster.exp;

import static com.ticketmaster.exp.Experiment.TrialType.CANDIDATE;
import static com.ticketmaster.exp.Experiment.TrialType.CONTROL;
import com.ticketmaster.exp.util.Assert;
import com.ticketmaster.exp.util.Tryz;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import javaslang.exception.Try;

/**
 * Created by dannwebster on 10/12/14.
 */
public class Experiment<T, M> implements Callable<Result<T>> {
    public static enum TrialType {
        CONTROL, 
        CANDIDATE;
    }

    private final String name;
    private final Callable<T> control;
    private final Callable<T> candidate;
    private final Supplier<Boolean> selector;

    private final Comparator<M> comparator;
    private final Function<Optional<T>, M> cleaner;

    private final Supplier<TrialType> whichIsFirst;
    private final ExecutorService executorService;
    private final Publisher<T> publisher;

    public Experiment(
            String name, 
            Callable<T> control, 
            Callable<T> candidate, 
            Supplier<Boolean> selector,
            Comparator<M> comparator, 
            Function<Optional<T>, M> cleaner,
            Supplier<TrialType> whichIsFirst,
            ExecutorService executorService,
            Publisher<T> publisher) {

        Assert.hasText(name, "name must be non-null and have text");
        Assert.notNull(control, "control must be non-null");
        Assert.notNull(candidate, "candidate must be non-null");
        Assert.notNull(selector, "selector must be non-null");
        Assert.notNull(comparator, "comparator must be non-null");
        Assert.notNull(cleaner, "cleaner must be non-null");
        Assert.notNull(whichIsFirst, "whichIsFirst must be non-null");
        Assert.notNull(executorService, "executorService must be non-null");
        // executor is optional
        // publisher is optional

        this.name = name;
        this.control = control;
        this.candidate = candidate;
        this.selector = selector;
        this.comparator = comparator;
        this.cleaner = cleaner;
        this.whichIsFirst = whichIsFirst;
        this.executorService = executorService;
        this.publisher = publisher;
    }


    public final T perform() {
        T value;
        try {
            if (selector.get()) {
                Result<T> result = call();
                Try<T> tryResult = result.getControlResult().getTryResult();
                value =  tryResult.get();
            } else {
                return control.call();
            }
        } catch (Exception e) {
            throw (e instanceof RuntimeException) ? 
                    (RuntimeException) e : 
                    new RuntimeException(e); 
        }
        return value;
    }
    @Override
    public final Result<T> call() throws Exception {
        TrialType first;
        Future<T> controlFuture;
        Future<T> candidateFuture;

        Instant timestamp = Instant.now();
        if (CONTROL.equals(whichIsFirst.get())) {
            first = CONTROL;
            controlFuture = executorService.submit(control);
            candidateFuture = executorService.submit(candidate);
        } else {
            first = CANDIDATE;
            candidateFuture = executorService.submit(candidate);
            controlFuture = executorService.submit(control);
        }

        TrialResult<T> candidateResult = observe(CANDIDATE, candidateFuture);
        TrialResult<T> controlResult = observe(CONTROL, controlFuture);
        Result<T> result = new Result<>(name, first, timestamp, candidateResult, controlResult);

        if (publisher != null) {
            boolean outputMatches = determineMatch(result);
            publisher.publish(outputMatches, result);
        }
        return result;
    }

    public boolean determineMatch(Result<T> result) {
        Optional<T> optCandidate = Tryz.optional(result.getCandidateResult().getTryResult());
        Optional<M> cleanCandidate =  Optional.ofNullable(cleaner.apply(optCandidate));

        Optional<T> optControl = Tryz.optional(result.getCandidateResult().getTryResult());
        Optional<M> cleanControl =  Optional.ofNullable(cleaner.apply(optControl));

        // take care of null-handling so comparators can be written more easily
        boolean isMatch = 
                (!cleanCandidate.isPresent() && !cleanControl.isPresent()) ||
                (
                    cleanCandidate.isPresent() && cleanControl.isPresent() &&
                    comparator.compare(cleanCandidate.get(), cleanControl.get()) == 0
                );
        return isMatch;
    }

    final TrialResult<T> observe(TrialType trialType, Future<T> future) {
        Instant start = Instant.now();
        Exception exception = null;
        T value = null;
        try {
            value = future.get();
        } catch (Exception t) {
            exception = t;
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        return new TrialResult<>(trialType, duration, exception, value);
    }
}
