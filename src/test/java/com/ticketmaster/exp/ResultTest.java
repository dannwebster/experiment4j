package com.ticketmaster.exp;

import com.ticketmaster.exp.util.SameWhens;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static org.junit.Assert.*;

public class ResultTest {
    @Test
    public void testResult() throws Exception {
        // GIVEN
        Duration candidateD = Duration.ofSeconds(5);
        Duration controlD = Duration.ofSeconds(10);

        TrialResult<String> candidate = new TrialResult(CANDIDATE, candidateD, null, "foo");
        TrialResult<String> control = new TrialResult(CONTROL, controlD, null, "bar");

        Instant inst = Instant.ofEpochMilli(0);
        // WHEN
        Result<String> result = new Result<>(
                "exp",
                inst,
                candidate,
                control);

        // THEN
        assertEquals(candidate, result.getCandidateResult());
        assertEquals(control, result.getControlResult());
        assertEquals("exp", result.getName());
        assertEquals(inst, result.getTimestamp());

    }

    @Test
    public void testDetermineMatchShouldReturnFalseWhenResultsDoNotMatch() throws Exception {
        // GIVEN
        TrialResult<String> controlTrialResult = new TrialResult<>(TrialType.CONTROL, Duration.ofMillis(1), null, "foo");
        TrialResult<String> candidateTrialResult = new TrialResult<>(TrialType.CANDIDATE, Duration.ofMillis(1), null, "bar");
        Result<String> result = new Result<>("exp", Instant.now(), candidateTrialResult, controlTrialResult);

        // WHEN
        MatchType matchType = result.determineMatch(a -> a, Object::equals, Objects::equals);

        // THEN
        assertEquals(MatchType.MISMATCH, matchType);
    }

    @Test
    public void testDetermineMatchShouldReturnMatchWhenResultsMatch() throws Exception {
        // GIVEN
        TrialResult<String> controlTrialResult = new TrialResult<>(TrialType.CONTROL, Duration.ofMillis(1),
                null, "foo");
        TrialResult<String> candidateTrialResult = new TrialResult<>(TrialType.CANDIDATE, Duration.ofMillis(1),
                null, "foo");
        Result<String> result = new Result<>("exp", Instant.now(), candidateTrialResult, controlTrialResult);

        // WHEN
        MatchType matchType = result.determineMatch(a -> a, Object::equals, Objects::equals);

        // THEN
        assertEquals(MatchType.MATCH, matchType);
    }

    @Test
    public void testDetermineMatchShouldReturnCandidateExceptionWhenOneReturnsException() throws Exception {
        // GIVEN
        TrialResult<String> controlTrialResult = new TrialResult<>(TrialType.CONTROL, Duration.ofMillis(1),
                null, "foo");
        TrialResult<String> candidateTrialResult = new TrialResult<>(TrialType.CANDIDATE, Duration.ofMillis(1),
                new IllegalArgumentException(), null);
        Result<String> result = new Result<>("exp", Instant.now(), candidateTrialResult, controlTrialResult);

        // WHEN
        MatchType matchType = result.determineMatch(a -> a, Object::equals, Objects::equals);

        // THEN
        assertEquals(MatchType.CANDIDATE_EXCEPTION, matchType);
    }

    @Test
    public void testDetermineMatchShouldReturnControlExceptionWhenOneReturnsException() throws Exception {
        // GIVEN
        TrialResult<String> controlTrialResult = new TrialResult<>(TrialType.CONTROL, Duration.ofMillis(1),
                new IllegalArgumentException(), null);
        TrialResult<String> candidateTrialResult = new TrialResult<>(TrialType.CANDIDATE, Duration.ofMillis(1),
            null, "foo");
        Result<String> result = new Result<>("exp", Instant.now(), candidateTrialResult, controlTrialResult);

        // WHEN
        MatchType matchType = result.determineMatch(a -> a, Object::equals, Objects::equals);

        // THEN
        assertEquals(MatchType.CONTROL_EXCEPTION, matchType);
    }

    @Test
    public void testDetermineMatchShouldReturnExceptionMatchWhenBothReturnSameExceptions() throws Exception {
        // GIVEN
        TrialResult<String> controlTrialResult = new TrialResult<>(TrialType.CONTROL, Duration.ofMillis(1),
                new IllegalArgumentException(), null);
        TrialResult<String> candidateTrialResult = new TrialResult<>(TrialType.CANDIDATE, Duration.ofMillis(1),
                new IllegalArgumentException(), null);
        Result<String> result = new Result<>("exp", Instant.now(), candidateTrialResult, controlTrialResult);

        // WHEN
        MatchType matchType = result.determineMatch(a -> a, Object::equals, SameWhens.classesMatch());

        // THEN
        assertEquals(MatchType.EXCEPTION_MATCH, matchType);
    }

    @Test
    public void testDetermineMatchShouldReturnExceptionMisMatchWhenBothReturnDifferentExceptions() throws Exception {
        // GIVEN
        TrialResult<String> controlTrialResult = new TrialResult<>(TrialType.CONTROL, Duration.ofMillis(1),
                new ArrayIndexOutOfBoundsException(), null);
        TrialResult<String> candidateTrialResult = new TrialResult<>(TrialType.CANDIDATE, Duration.ofMillis(1),
                new IllegalArgumentException(), null);
        Result<String> result = new Result<>("exp", Instant.now(), candidateTrialResult, controlTrialResult);

        // WHEN
        MatchType matchType = result.determineMatch(a -> a, Object::equals, Objects::equals);

        // THEN
        assertEquals(MatchType.EXCEPTION_MISMATCH, matchType);

    }
}