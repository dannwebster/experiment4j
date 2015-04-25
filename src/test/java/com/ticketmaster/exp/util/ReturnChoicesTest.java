package com.ticketmaster.exp.util;

import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.TrialResult;
import com.ticketmaster.exp.TrialType;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.IllegalFormatCodePointException;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static org.junit.Assert.*;

public class ReturnChoicesTest {
    // reduce coverage noise
    ReturnChoices returnChoices = new ReturnChoices();

    TrialResult<String> goodCandidate = new TrialResult<>(CANDIDATE, Duration.ofMillis(200), null, "candidate");
    TrialResult<String> fastGoodCandidate = new TrialResult<>(CANDIDATE, Duration.ofMillis(100), null, "candidate");
    TrialResult<String> fasterGoodCandidate = new TrialResult<>(CANDIDATE, Duration.ofMillis(0), null, "candidate");
    TrialResult<String> badCandidate = new TrialResult<>(CANDIDATE, Duration.ofMillis(200), new IllegalArgumentException(), null);
    TrialResult<String> goodControl = new TrialResult<>(CONTROL, Duration.ofMillis(100), null, "control");
    TrialResult<String> badControl = new TrialResult<>(CONTROL, Duration.ofMillis(100), new IllegalArgumentException(), null);

    Instant instant = Instant.EPOCH;
    @Test
    public void testAlwaysControlShouldAlwaysReturnControl() throws Exception {

        // GIVEN
        Result<String> result = new Result<>("exp", instant, goodCandidate, goodControl);

        // WHEN
        Try<String> t = ReturnChoices.<String>alwaysControl().apply(result);

        // THEN
        assertSame("control", t.get());

    }

    @Test
    public void testAlwaysCandidateShouldAlwaysReturnCandidate() throws Exception {

        // GIVEN
        Result<String> result = new Result<>("exp", instant, goodCandidate, goodControl);

        // WHEN
        Try<String> t = ReturnChoices.<String>alwaysCandidate().apply(result);

        // THEN
        assertSame("candidate", t.get());
    }

    @Test
    public void testFindFastestShouldReturnFastest() throws Exception {

        // GIVEN
        Result<String> result = new Result<>("exp", instant, goodCandidate, goodControl);

        // WHEN
        Try<String> t = ReturnChoices.<String>findFastest().apply(result);

        // THEN
        // control duration is 100, candidate is 200
        assertSame("control", t.get());
    }


    @Test
    public void testFindFastestShouldReturnFastestCandidate() throws Exception {

        // GIVEN
        Result<String> result = new Result<>("exp", instant, fasterGoodCandidate, goodControl);

        // WHEN
        Try<String> t = ReturnChoices.<String>findFastest().apply(result);

        // THEN
        // control duration is 100, candidate is 200
        assertSame("candidate", t.get());
    }

    @Test
    public void testFindBestShouldReturnFastestWhenBothAreGood() throws Exception {

        // GIVEN
        Result<String> result = new Result<>("exp", instant, goodCandidate, goodControl);

        // WHEN
        Try<String> choice = ReturnChoices.<String>findBest().apply(result);

        // THEN
        assertSame("control", choice.get());
    }

    @Test
    public void testFindBestShouldReturnNonErrorCandidate() throws Exception {
        // GIVEN
        Result<String> badControlResult = new Result<>("exp", instant, goodCandidate, badControl);

        // WHEN
        Try<String> choice = ReturnChoices.<String>findBest().apply(badControlResult);

        // THEN
        assertSame("candidate", choice.get());
    }

    @Test
    public void testFindBestShouldReturnNonErrorControl() throws Exception {
        // GIVEN
        Result<String> badCandidateResult = new Result<>("exp", instant, badCandidate, goodControl);

        // WHEN
        Try<String> choice = ReturnChoices.<String>findBest().apply(badCandidateResult);

        // THEN
        assertSame("control", choice.get());
    }

    @Test
    public void testFindBestShouldControlWhenBothAreGoodAndSameSpeed() throws Exception {
        // GIVEN
        Result<String> badCandidateResult = new Result<>("exp", instant, fastGoodCandidate, goodControl);

        // WHEN
        Try<String> choice = ReturnChoices.<String>findBest().apply(badCandidateResult);

        // THEN
        assertSame("control", choice.get());
    }

    @Test
    public void testCandidateWhenReturnsCandidateWhenSelectorIsTrue() throws Exception {

        // GIVEN
        Result<String> result = new Result<>("exp", instant, goodCandidate, goodControl);

        // WHEN
        Try<String> choice = ReturnChoices.<String>candidateWhen(Selectors.always()).apply(result);

        // THEN
        assertSame("candidate", choice.get());
    }

    @Test
    public void testCandidateWhenReturnsControlWhenSelectorIsFalse() throws Exception {

        // GIVEN
        Result<String> result = new Result<>("exp", instant, goodCandidate, goodControl);

        // WHEN
        Try<String> choice = ReturnChoices.<String>candidateWhen(Selectors.never()).apply(result);

        // THEN
        assertSame("control", choice.get());
    }
}