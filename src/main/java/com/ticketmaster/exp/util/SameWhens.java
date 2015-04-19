package com.ticketmaster.exp.util;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Created by dannwebster on 4/19/15.
 */
public class SameWhens {
    SameWhens() {}

    public static <M> BiFunction<M, M, Boolean> fromComparator(Comparator<M> comparator) {
        return (M m1, M m2) -> comparator.compare(m1, m2) == 0;
    }

}
