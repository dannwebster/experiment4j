package com.ticketmaster.exp.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by dannwebster on 4/17/15.
 */
public class Duple<E> {
    private final E e1;
    private final E e2;
    private final List<E> l;

    public static <E> Duple<E> from(E e1, E e2) {
        return new Duple<>(e1, e2);
    }

    private Duple(E e1, E e2) {
        Assert.notNull(e1, "first element must be non null");
        Assert.notNull(e2, "second element must be non null");
        this.e1 = e1;
        this.e2 = e2;
        l = Arrays.asList(e1, e2);
    }

    public Stream<E> parallelStream() {
        return l.parallelStream();
    }
    public Stream<E> stream() {
        return l.stream();
    }

    public E getE1() {
        return e1;
    }

    public E getE2() {
        return e2;
    }
}
