package com.ticketmaster.exp.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertEquals;

/**
 * Created by dannwebster on 4/18/15.
 */
public class SelectorsTest {
    // removes noise from coverage results
    Selectors s = new Selectors();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testPermilleOver1000ShouldThrowException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("value 1001 is not between bounds of 0 (inclusive) and 1001 (exclusive)");
        Selectors.permille(1001);
    }

    @Test
    public void testPermilleUnder0ShouldThrowException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("value -1 is not between bounds of 0 (inclusive) and 1001 (exclusive)");
        Selectors.permille(-1);
    }

    @Test
    public void testPercentOver100ShouldThrowException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("value 101 is not between bounds of 0 (inclusive) and 101 (exclusive)");
        Selectors.percent(101);
    }

    @Test
    public void testPercentUnder0ShouldThrowException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("value -1 is not between bounds of 0 (inclusive) and 101 (exclusive)");
        Selectors.percent(-1);
    }

    @Test
    public void testConstantThresholds() throws Exception {
        assertEquals(true, Selectors.always().getAsBoolean());
        assertEquals(false, Selectors.never().getAsBoolean());
    }

    @Test
    public void testPercentAlwaysReturnTrueForMaxThreshold() throws Exception {
        // Given
        BooleanSupplier percent = Selectors.percent(100);

        // When
        boolean pass = percent.getAsBoolean();

        // Then
        assertEquals(true, pass);

    }

    @Test
    public void testPermilleAlwaysReturnTrueForMaxThreshold() throws Exception {
        // Given
        BooleanSupplier permille = Selectors.permille(1000);

        // When
        boolean pass = permille.getAsBoolean();

        // Then
        assertEquals(true, pass);

    }

    @Test
    public void testPercentNeverReturnTrueForThreshold0() throws Exception {
        // Given
        BooleanSupplier percent = Selectors.percent(0);

        // When
        boolean pass = percent.getAsBoolean();

        // Then
        assertEquals(false, pass);
    }

    @Test
    public void testPermilleNeverReturnTrueForThreshold0() throws Exception {
        // Given
        BooleanSupplier permille = Selectors.permille(0);

        // When
        boolean pass = permille.getAsBoolean();

        // Then
        assertEquals(false, pass);
    }

    @Test
    public void testPercentOfObjectHashWithMinPercentIsNeverTrue() throws Exception {

        // GIVEN
        Object o = new Object() {
            public int hashCode() {
                return 100;
            }
        };
        BooleanSupplier percent = Selectors.percentOfObjectHash(0, () -> o);

        // WHEN
        boolean pass = percent.getAsBoolean();


        // THEN
        assertEquals(false, pass);
    }
    @Test
    public void testPercentOfObjectHashWithMaxPercentIsAlwaysTrue() throws Exception {
        
        // GIVEN
        Object o = new Object() {
            public int hashCode() {
                return 100;
            }
        };
        BooleanSupplier percent = Selectors.percentOfObjectHash(100, () -> o);
        
        // WHEN
        boolean pass = percent.getAsBoolean();

        
        // THEN
        assertEquals(true, pass);
    }

    @Test
    public void testPermilleOfObjectHashWithMinPermilleIsNeverTrue() throws Exception {

        // GIVEN
        Object o = new Object() {
            public int hashCode() {
                return 1000;
            }
        };
        BooleanSupplier permille = Selectors.permilleOfObjectHash(0, () -> o);

        // WHEN
        boolean pass = permille.getAsBoolean();


        // THEN
        assertEquals(false, pass);
    }
    @Test
    public void testPermilleOfObjectHashWithMaxPermilleIsAlwaysTrue() throws Exception {

        // GIVEN
        Object o = new Object() {
            public int hashCode() {
                return 1000;
            }
        };
        BooleanSupplier permille = Selectors.permilleOfObjectHash(1000, () -> o);

        // WHEN
        boolean pass = permille.getAsBoolean();


        // THEN
        assertEquals(true, pass);
    }
}
