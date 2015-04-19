package com.ticketmaster.exp;

import org.junit.Test;

import static org.junit.Assert.*;

public class TrialTypeTest {
    @Test
    public void testValueOf() throws Exception {

        // EXPECT
        assertEquals(TrialType.CANDIDATE, TrialType.valueOf("CANDIDATE"));
        assertEquals(TrialType.CONTROL, TrialType.valueOf("CONTROL"));
        assertEquals(2, TrialType.values().length);


    }

}