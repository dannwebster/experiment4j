package com.ticketmaster.exp.util;

import org.junit.Test;
import org.mockito.internal.matchers.Same;

import java.util.Comparator;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

/**
 * Created by dannwebster on 4/19/15.
 */
public class SameWhensTest {
    // remove coverage noise
    SameWhens sameWhens = new SameWhens();
    @Test
    public void testForComparator() throws Exception {

        // GIVEN
        Comparator<String> comp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        // WHEN
        BiFunction<String, String, Boolean> biFunction = SameWhens.fromComparator(comp);

        // THEN
        assertEquals(true, biFunction.apply("foo", "foo"));
        assertEquals(false, biFunction.apply("foo", "bar"));
    }
    @Test
    public void testSameClass() throws Exception {

        assertEquals(true, SameWhens.classesMatch().apply("foo", "bar"));
        assertEquals(false, SameWhens.classesMatch().apply("foo", new Integer(1)));
        assertEquals(false, SameWhens.classesMatch().apply(null, new Integer(1)));
        assertEquals(false, SameWhens.classesMatch().apply("foo", null));


    }
}
