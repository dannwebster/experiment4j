package com.ticketmaster.exp.util;

import java.util.Optional;

import javaslang.exception.Try;

/**
 *
 * @author dannwebster
 */
public class Tryz {
    public static <T> Try<T> of(final T val, final Throwable e) {
        return Try.of( 
                () -> {
                    if(e == null) { 
                        return val;
                    } else {
                        throw e;
                    }

        });
    }

    public static <T> Optional<T> optional(Try<T> t) {
        return Optionalz.optional(t.toOption());
    }

}
