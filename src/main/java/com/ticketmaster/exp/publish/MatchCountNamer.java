package com.ticketmaster.exp.publish;

/**
 *
 * @author dannwebster
 */
public interface MatchCountNamer<K> {
    K name(String experimentName, boolean matches);
}
