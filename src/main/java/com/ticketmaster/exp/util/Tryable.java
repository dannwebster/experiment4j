package com.ticketmaster.exp.util;

import java.util.concurrent.Callable;

/**
 * Created by dannwebster on 4/17/15.
 */
public interface Tryable<T> {
    public Try<T> call();

    public static <T> Tryable<T> tryable(Callable<T> c) {
        return new Tryable<T>() {
            @Override
            public Try<T> call() {
                return Try.from(c);
            }
        };
    }
}
