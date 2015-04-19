package com.ticketmaster.exp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dannwebster on 4/18/15.
 */
public class SelectorsTest {
    // removes noise from coverage results
    Selectors s = new Selectors();

    @Test
    public void testSelectors() throws Exception {
        assertEquals(true, Selectors.ALWAYS.getAsBoolean());
        assertEquals(false, Selectors.NEVER.getAsBoolean());
        assertEquals(true, Selectors.percentage(100).getAsBoolean());
        assertEquals(false, Selectors.percentage(0).getAsBoolean());
    }
}
