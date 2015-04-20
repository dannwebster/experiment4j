package com.ticketmaster.exp.publish;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConcurrentMapMatchCounterTest {

    @Test
    public void testGetAndIncrementOnEmptyMap() throws Exception {

        // GIVEN
        ConcurrentMapMatchCounter<String> ccmc = new ConcurrentMapMatchCounter<>();
        assertEquals(true, ccmc.getCounts().isEmpty());

        // WHEN
        int count = ccmc.getAndIncrement("foo");

        // THEN
        assertEquals(1, count);
        assertEquals(1, ccmc.getMatchCount("foo").intValue());
        assertEquals(1, ccmc.getCounts().size());
        assertEquals(true, ccmc.getCounts().containsKey("foo"));
    }

    @Test
    public void testGetAndIncrementOnExistingValue() throws Exception {

        // GIVEN
        ConcurrentMapMatchCounter<String> ccmc = new ConcurrentMapMatchCounter<>();
        assertEquals(true, ccmc.getCounts().isEmpty());
        int count = ccmc.getAndIncrement("foo");

        // WHEN
        count = ccmc.getAndIncrement("foo");

        // THEN
        assertEquals(2, count);
        assertEquals(2, ccmc.getMatchCount("foo").intValue());
        assertEquals(1, ccmc.getCounts().size());
        assertEquals(true, ccmc.getCounts().containsKey("foo"));
    }

}