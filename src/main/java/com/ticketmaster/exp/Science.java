package com.ticketmaster.exp;

import com.ticketmaster.exp.Experiment.TrialType;
import static com.ticketmaster.exp.Experiment.TrialType.CANDIDATE;
import static com.ticketmaster.exp.Experiment.TrialType.CONTROL;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by dannwebster on 10/12/14.
 */
public class Science<T, M> {
    public static final Random INTS = new Random(Instant.now().toEpochMilli());
    private static final ConcurrentMap<String, Science<?, ?>> CACHE = new ConcurrentHashMap<>();

    private String name;
    private Callable<T> control;
    private Callable<T> candidate;
    private Supplier<Boolean> selector = () -> true;
    private Publisher<T> publisher = null;

    // equals comparator
    private Comparator<M> comparator = (M a, M b) -> (a.equals(b)) ? 1 : 0;

    // no-op function
    private Function<Optional<T>, M> cleaner = (Optional<T> t) -> (M)(t.orElse(null));
    private Supplier<TrialType> whichIsFirst = () -> INTS.nextInt(2) == 0 ? CONTROL : CANDIDATE;
    private ExecutorService executorService = new ThreadPoolExecutor(2, 2, 1, TimeUnit.DAYS, new ArrayBlockingQueue(2));

    private Science () {};

    public static <T, M> T perform(Experiment<T, M> experiment) {
        return experiment.perform();
    }

    public static <T, M> Science<T, M> science(String name, Consumer<Science<T, M>> configurer) {
        Science<T, M> b = (Science<T, M>) CACHE.get(name);
        if (b == null) {
            b = new Science<>();
            CACHE.put(name, b);
        }
        b.named(name);
        configurer.accept(b);
        return b;
    }

    public Experiment<T, M> expermient(Consumer<Science<T, M>> configurer) {
        configurer.accept(this);
        return new Experiment<>(name, control, candidate, selector, comparator, cleaner, whichIsFirst, executorService, publisher);
    }

    public Experiment<T, M> expermient() {
        return new Experiment<>(name, control, candidate, selector, comparator, cleaner, whichIsFirst, executorService, publisher);
    }
    


    public Science<T, M> named(String name) {
        this.name = name;
        return this;
    }

    public Science<T, M> control(Callable<T> control) {
        this.control = control;
        return this;
    }

    public Science<T, M> candidate(Callable<T> candidate){
        this.candidate = candidate;
        return this;
    }

    public Science<T, M> selector(Supplier<Boolean> selector) {
        this.selector = selector;
        return this;
    }

    public Science<T, M> comparator(Comparator<M> comparator) {
        this.comparator = comparator;
        return this;

    }

    public Science<T, M> cleaner(Function<Optional<T>, M> cleaner) {
        this.cleaner = cleaner;
        return this;

    }
    public Science<T, M> publisher(Publisher<T> publisher) {
        this.publisher = publisher;
        return this;
    }

    public Science<T, M> executorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public Science<T, M> whichIsFirst(Supplier<TrialType> whichIsFirst) {
        this.whichIsFirst = whichIsFirst;
        return this;
    }

   
}
