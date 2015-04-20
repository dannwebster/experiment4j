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

    public <M> M doExperiment(String name) throws Exception {
        return (M) getExperiment(name)
                .orElseGet(() -> null)
                .call();
    }

    public <T, M> Experiment<T, M> experiment(String name, Supplier<? extends Experiment> experimentBuilder) {
        return cache.computeIfAbsent(name, (k) -> experimentBuilder.get() );
    }

    public <T, M> Optional<Experiment<T, M>> getExperiment(String name) {
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
