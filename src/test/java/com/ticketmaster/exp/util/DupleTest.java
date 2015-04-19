package com.ticketmaster.exp.util;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class DupleTest {

    @Test
    public void testFrom() throws Exception {
        // GIVEN
        Duple<String> d = Duple.from("s1", "s2");

        // THEN
        assertEquals("s1", d.getE1());
        assertEquals("s2", d.getE2());
        Iterator<String> i = d.stream().iterator();

        assertEquals("s1", i.next());
        assertEquals("s2", i.next());
        assertEquals(false, i.hasNext());

        i = d.parallelStream().iterator();
        i.next();
        i.next();
        assertEquals(false, i.hasNext());

    }
}