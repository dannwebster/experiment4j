package com.ticketmaster.exp;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

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
}