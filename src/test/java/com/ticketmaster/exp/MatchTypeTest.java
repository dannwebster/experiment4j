package com.ticketmaster.exp;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatchTypeTest {
    @Test
    public void testValuesAndValueOf() throws Exception {
        assertEquals(6, MatchType.values().length);
        assertEquals(MatchType.CANDIDATE_EXCEPTION, MatchType.valueOf("CANDIDATE_EXCEPTION"));
    }

}