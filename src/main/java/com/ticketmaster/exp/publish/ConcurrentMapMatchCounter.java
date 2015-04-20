package com.ticketmaster.exp.publish;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dannwebster on 4/19/15.
 */
public class ConcurrentMapMatchCounter<K> implements MatchCounter<K>{
    private final Map<K, Integer> counts = new ConcurrentHashMap<>();

    public Integer getMatchCount(K matchKey) {
        return counts.get(matchKey);
    }

    public int getAndIncrement(K matchKey) {
        int matchCount = counts.compute(matchKey, (k, v) -> v == null ? 1 : v++ );
        return matchCount;
    }

    public Map<K, Integer> getCounts() {
        return Collections.unmodifiableMap(counts);
    }
}
