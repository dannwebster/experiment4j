package com.ticketmaster.exp.util;

import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AssertTest {

    // Removes noise from coverage results
    Assert a = new Assert();

    @Rule
    public ExpectedException ex = ExpectedException.none();

    @Test
    public void testHasTextShouldThrowExceptionWhenNull() throws Exception {
        // EXPECT
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("has text");

        // WHEN
        Assert.hasText(null, "has text");
    }

    @Test
    public void testHasTextShouldThrowExceptionWhenEmpty() throws Exception {
        // EXPECT
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("has text");

        // WHEN
        Assert.hasText("", "has text");
    }

    @Test
    public void testHasTextShouldSucceedWhenHasText() throws Exception {
        Assert.hasText("s", "has text");
    }
}
