package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.MatchType;

/**
 *
 * @author dannwebster
 */
public interface MatchCountNamer<K> {
    K name(String experimentName, MatchType matchType);
}
