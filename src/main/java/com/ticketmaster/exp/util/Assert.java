package com.ticketmaster.exp.util;

/**
 *
 * @author dannwebster
 */
public class Assert {
    Assert() {}

    public static void between(int value, int minInclusive, int maxExclusive) {
        if (value < minInclusive || value >= maxExclusive) {
            throw new IllegalArgumentException(
                    String.format("value %d is not between bounds of %d (inclusive) and %d (exclusive)", value, minInclusive, maxExclusive)
            );
        }
    }
    public static void notNull(Object s, String msg) {
        if (s == null) {
            throw new IllegalArgumentException(msg);
        }
    };

    public static void hasText(String s, String msg) { 
        notNull(s, msg); 
        if (s.isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
    };

}
