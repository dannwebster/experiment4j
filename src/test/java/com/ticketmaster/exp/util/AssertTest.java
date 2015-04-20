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
    public void testInRangeShouldThrowExceptionWhenEqualsUpperBound() throws Exception {
        // EXPECT
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("value 100 is not between bounds of 0 (inclusive) and 100 (exclusive)");

        // GIVEN
        Assert.between(100, 0, 100);
    }

    @Test
    public void testInRangeShouldThrowExceptionWhenOverUpperBound() throws Exception {
        // EXPECT
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("value 101 is not between bounds of 0 (inclusive) and 100 (exclusive)");

        // GIVEN
        Assert.between(101, 0, 100);
    }

    @Test
    public void testInRangeShouldThrowExceptionWhenUnderLowerBound() throws Exception {
        // EXPECT
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("value -1 is not between bounds of 0 (inclusive) and 100 (exclusive)");

        // GIVEN
        Assert.between(-1, 0, 100);
    }

    @Test
    public void testInRangeShouldNotThrowExceptionWhenInRange() throws Exception {
        // MIDDLE
        Assert.between(50, 0, 100);

        // LOWER BOUND
        Assert.between(0, 0, 100);

        // UPPER BOUND
        Assert.between(99, 0, 100);
    }

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
