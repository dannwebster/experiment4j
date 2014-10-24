package com.crypticmission.exp.util;

import java.util.Optional;
import javaslang.exception.Cause;
import javaslang.exception.Try;
import javaslang.exception.Try.CheckedSupplier;

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
