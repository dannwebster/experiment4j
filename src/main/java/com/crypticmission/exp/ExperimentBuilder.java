package com.crypticmission.exp;

import com.crypticmission.exp.Experiment.TrialType;
import static com.crypticmission.exp.Experiment.TrialType.CANDIDATE;
import static com.crypticmission.exp.Experiment.TrialType.CONTROL;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by dannwebster on 10/12/14.
 */
public class ExperimentBuilder<T, M> {
    public static final Random INTS = new Random(Instant.now().toEpochMilli());
    private String name;
    private Callable<T> control;
    private Callable<T> candidate;
    private Callable<Boolean> selector = () -> true;
    private Publisher<T> publisher = null;

    // equals comparator
    private Comparator<M> comparator = (M a, M b) -> (a.equals(b)) ? 1 : 0;

    // no-op function
    private Function<Optional<T>, M> cleaner = (Optional<T> t) -> (M)(t.orElse(null));
    private Callable<TrialType> whichIsFirst = () -> INTS.nextInt(2) == 0 ? CONTROL : CANDIDATE;
    private ExecutorService executorService = new ThreadPoolExecutor(2, 2, 1, TimeUnit.DAYS, new ArrayBlockingQueue(2));

    private ExperimentBuilder () {};

    public static <T, M> ExperimentBuilder<T, M> experiment() {
        return new ExperimentBuilder<>();
    }

    public ExperimentBuilder<T, M> named(String name) {
        this.name = name;
        return this;
    }

    public ExperimentBuilder<T, M> control(Callable<T> control) {
        this.control = control;
        return this;
    }

    public ExperimentBuilder<T, M> candidate(Callable<T> candidate){
        this.candidate = candidate;
        return this;
    }

    public ExperimentBuilder<T, M> selector(Callable<Boolean> selector) {
        this.selector = selector;
        return this;
    }

    public ExperimentBuilder<T, M> comparator(Comparator<M> comparator) {
        this.comparator = comparator;
        return this;

    }
    public ExperimentBuilder<T, M> cleaner(Function<Optional<T>, M> cleaner) {
        this.cleaner = cleaner;
        return this;

    }
    public ExperimentBuilder<T, M> publisher(Publisher<T> publisher) {
        this.publisher = publisher;
        return this;
    }

    public ExperimentBuilder<T, M> executorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public ExperimentBuilder<T, M> whichIsFirst(Callable<TrialType> whichIsFirst) {
        this.whichIsFirst = whichIsFirst;
        return this;
    }

    public Experiment<T, M> build() {
        return new Experiment<>(name, control, candidate, selector, comparator, cleaner, whichIsFirst, executorService, publisher);
    }


    public static Experiment experiment(String name, Function<ExperimentBuilder, ExperimentBuilder> configurer) {
        ExperimentBuilder b = new ExperimentBuilder();
        b.named(name);
        b = configurer.apply(b);
        Experiment e = b.build();
        return e;
    }
}
