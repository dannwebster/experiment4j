package com.ticketmaster.exp.util;

import jdk.nashorn.internal.codegen.CompilerConstants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

/**
 * Created by dannwebster on 4/18/15.
 */
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
        Callable<String> c = () -> { throw new IllegalArgumentException(); };

        // WHEN
        Try<String> t = Try.from(c);

        // THEN
        assertEquals(true, t.exception().isPresent());
        assertEquals(false, t.value().isPresent());
        assertEquals(false, t.isSuccess());
        assertEquals(true, t.isFailure());
    }
}
