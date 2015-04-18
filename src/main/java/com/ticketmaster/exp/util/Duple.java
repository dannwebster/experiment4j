package com.ticketmaster.exp.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by dannwebster on 4/17/15.
 */
public class Duple<A> {
    private final A a1;
    private final A a2;
    private final List<A> l;

    public static <A> Duple<A> from(A a1, A a2) {
        return new Duple<>(a1, a2);
    }

    private Duple(A a1, A a2) {
        this.a1 = a1;
        this.a2 = a2;
        l = Arrays.asList(a1, a2);
    }

    public Stream<A> parallelStream() {
        return l.parallelStream();
    }
    public Stream<A> stream() {
        return l.stream();
    }

    public A getA1() {
        return a1;
    }

    public A getA2() {
        return a2;
    }
}
