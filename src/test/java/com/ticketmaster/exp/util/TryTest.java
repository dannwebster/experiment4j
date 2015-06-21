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

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

public class TryTest {
  @Rule
  public ExpectedException ex = ExpectedException.none();

  @Test
  public void testSuccessfulCallShouldReturnValue() throws Exception {

    // GIVEN
    Try<String> t = Try.of("s", null);

    // WHEN
    String s = t.call();

    // THEN
    assertEquals("s", s);
  }

  @Test
  public void testFailingCallWillFailWithException() throws Exception {


    // EXPECT
    ex.expect(IllegalArgumentException.class);
    ex.expectMessage("fail");

    // GIVEN
    Try<String> t = Try.of(null, new IllegalArgumentException("fail"));

    // WHEN
    t.call();
  }

  @Test
  public void testAllValuesShouldFail() throws Exception {
    // EXPECT
    ex.expect(IllegalArgumentException.class);
    ex.expectMessage("exactly one of value or exception must be non-null");

    // WHEN
    Try.of("s", new IllegalArgumentException());
  }

  @Test
  public void testAllNullShouldFail() throws Exception {
    // EXPECT
    ex.expect(IllegalArgumentException.class);
    ex.expectMessage("exactly one of value or exception must be non-null");

    // WHEN
    Try.of(null, null);
  }

  @Test
  public void testOfWithValueIsSuccess() throws Exception {
    // WHEN
    Try<String> t = Try.of("s", null);

    // THEN
    assertEquals(false, t.exception().isPresent());
    assertEquals(true, t.value().isPresent());
    assertEquals(true, t.isSuccess());
    assertEquals(false, t.isFailure());
  }

  @Test
  public void testOfWithExceptionIsFailure() throws Exception {
    // WHEN
    Try<String> t = Try.of(null, new IllegalArgumentException());

    // THEN
    assertEquals(true, t.exception().isPresent());
    assertEquals(false, t.value().isPresent());
    assertEquals(false, t.isSuccess());
    assertEquals(true, t.isFailure());
  }

  @Test
  public void testSuccessfulCallable() throws Exception {
    // GIVEN
    Callable<String> c = () -> "s";

    // WHEN
    Try<String> t = Try.from(c);

    // THEN
    assertEquals(false, t.exception().isPresent());
    assertEquals(true, t.value().isPresent());
    assertEquals(true, t.isSuccess());
    assertEquals(false, t.isFailure());
  }

  @Test
  public void testFailedCallable() throws Exception {
    // GIVEN
    Callable<String> c = () -> {
      throw new IllegalArgumentException();
    };

    // WHEN
    Try<String> t = Try.from(c);

    // THEN
    assertEquals(true, t.exception().isPresent());
    assertEquals(false, t.value().isPresent());
    assertEquals(false, t.isSuccess());
    assertEquals(true, t.isFailure());
  }

  @Test
  public void testGetOrThrowShouldThrowWhenHasException() throws Exception {
    // EXPECT
    ex.expect(Exception.class);
    ex.expectMessage("foo");

    // GIVEN
    Try<String> t = Try.of(null, new Exception("foo"));

    // WHEN
    t.getOrThrow();
  }

  @Test
  public void testGetOrThrowUncheckedShouldThrowRuntimeExceptionWhenHasCheckedException() throws Exception {
    // EXPECT
    ex.expect(RuntimeException.class);
    ex.expectMessage("foo");

    // GIVEN
    Try<String> t = Try.of(null, new Exception("foo"));

    // WHEN
    t.getOrThrowUnchecked();
  }

  @Test
  public void testGetOrThrowUncheckedShouldThrowOriginalExceptionWhenItIsUnchecked() throws Exception {
    // EXPECT
    ex.expect(IllegalArgumentException.class);
    ex.expectMessage("foo");

    // GIVEN
    Try<String> t = Try.of(null, new IllegalArgumentException("foo"));

    // WHEN
    t.getOrThrowUnchecked();
  }

  @Test
  public void testGetOrThrowMethodsShouldReturnValueWhenExists() throws Exception {
    // GIVEN
    Try<String> t = Try.of("foo", null);

    // THEN
    assertEquals("foo", t.getOrThrowUnchecked());
    assertEquals("foo", t.getOrThrow());
  }
}
