package com.ticketmaster.exp.util;

/**
 *
 * @author dannwebster
 */
public class Assert {
    Assert() {}

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
