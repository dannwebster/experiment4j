package com.ticketmaster.exp;

import com.ticketmaster.exp.util.Try;
import sun.security.pkcs.SigningCertificateInfo;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Created by dannwebster on 4/18/15.
 */
public class Science {
    private final Map<String, Experiment> cache = new ConcurrentHashMap<>();

    public static final Science INSTANCE = new Science();
    private Science() {}

    public static Science science() {
        return INSTANCE;
    }

    public <I, O> O doExperiment(String name, I args) throws Exception {
        return (O) getExperiment(name)
                .orElseGet(() -> null)
                .apply(args);
    }

    public <I, O, M> Experiment<I, O, M> experiment(String name, Supplier<? extends Experiment> experimentBuilder) {
        return cache.computeIfAbsent(name, (k) -> experimentBuilder.get() );
    }

    public <I, O, M> Optional<Experiment<I, O, M>> getExperiment(String name) {
        return Optional.ofNullable(cache.get(name));
    }

    public Map<String, Experiment> experiments() {
        return Collections.unmodifiableMap(cache);
    }

    public void getClearExperiments() {
        cache.clear();
    }

    public int getExperimentCount() {
        return cache.size();
    }
}
