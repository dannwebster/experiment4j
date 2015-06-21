/*
 * Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ticketmaster.exp.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class SelectorsTest {
  // removes noise from coverage results
  Selectors s = new Selectors();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testPermilleOver1000ShouldThrowException() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("value 1000 is not between bounds of 0 (inclusive) and 1000 (exclusive)");
    Selectors.permille(1000);
  }

  @Test
  public void testPermilleUnder0ShouldThrowException() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("value -1 is not between bounds of 0 (inclusive) and 1000 (exclusive)");
    Selectors.permille(-1);
  }

  @Test
  public void testPercentOver100ShouldThrowException() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("value 100 is not between bounds of 0 (inclusive) and 100 (exclusive)");
    Selectors.percent(100);
  }

  @Test
  public void testPercentUnder0ShouldThrowException() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("value -1 is not between bounds of 0 (inclusive) and 100 (exclusive)");
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
    BooleanSupplier percent = Selectors.percent(99);

    for (int i = 0; i < 10000; i++) {
      // When
      boolean pass = percent.getAsBoolean();

      // Then
      assertEquals("failed for value " + i, true, pass);
    }

  }

  @Test
  public void testPermilleAlwaysReturnTrueForMaxThreshold() throws Exception {
    // Given
    BooleanSupplier permille = Selectors.permille(999);

    for (int i = 0; i < 10000; i++) {
      // When
      boolean pass = permille.getAsBoolean();

      // Then
      assertEquals("failed for value " + i, true, pass);
    }


  }

  @Test
  public void testPercentNeverReturnTrueForThreshold0() throws Exception {
    // Given
    BooleanSupplier percent = Selectors.percent(0);

    for (int i = 0; i < 10000; i++) {
      // When
      boolean pass = percent.getAsBoolean();

      // Then
      assertEquals(false, pass);
    }
  }

  @Test
  public void testPermilleNeverReturnTrueForThreshold0() throws Exception {
    // Given
    BooleanSupplier permille = Selectors.permille(0);

    for (int i = 0; i < 10000; i++) {
      // When
      boolean pass = permille.getAsBoolean();

      // Then
      assertEquals("failed for value " + i, false, pass);
    }
  }

  @Test
  public void testPercentOfObjectHashWithMinPercentIsNeverTrue() throws Exception {
    // GIVEN
    int max = 100;

    BooleanSupplier percent = Selectors.percentOfObjectHash(0, objectWithHash(max));

    for (int i = 0; i < max; i++) {
      // WHEN
      boolean pass = percent.getAsBoolean();
      // THEN
      assertEquals("failed for value " + i, false, pass);
    }
  }

  @Test
  public void testPercentOfObjectHashWithMaxPercentIsAlwaysTrue() throws Exception {
    // GIVEN
    int max = 100;

    BooleanSupplier percent = Selectors.percentOfObjectHash(99, objectWithHash(max));

    for (int i = 0; i < max; i++) {
      // WHEN
      boolean pass = percent.getAsBoolean();
      // THEN
      assertEquals("failed for value " + i, true, pass);
    }
  }

  @Test
  public void testPermilleOfObjectHashWithMinPermilleIsNeverTrue() throws Exception {
    // GIVEN
    int max = 1000;

    BooleanSupplier permille = Selectors.permilleOfObjectHash(0, objectWithHash(max));

    for (int i = 0; i < max; i++) {
      // WHEN
      boolean pass = permille.getAsBoolean();
      // THEN
      assertEquals("failed for value " + i, false, pass);
    }

  }

  @Test
  public void testPermilleOfObjectHashWithMaxPermilleIsAlwaysTrue() throws Exception {

    // GIVEN
    int max = 1000;

    BooleanSupplier permille = Selectors.permilleOfObjectHash(max - 1, objectWithHash(max));

    for (int i = 0; i < max; i++) {
      // WHEN
      boolean pass = permille.getAsBoolean();
      // THEN
      assertEquals("failed for value " + i, true, pass);
    }


  }

  public Object objectWithHash(int max) {
    return new Object() {
      Iterator<Integer> range = IntStream.range(0, max).iterator();
      public int hashCode() {
        return range.next();
      }
    };
  }

}
