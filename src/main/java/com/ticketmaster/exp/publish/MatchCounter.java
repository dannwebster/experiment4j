package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.MatchType;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dannwebster on 4/19/15.
 */
public interface MatchCounter<K> {
    public Integer getMatchCount(K matchKey);
    public int getAndIncrement(K matchKey);
    public Map<K, Integer> getCounts();
}
