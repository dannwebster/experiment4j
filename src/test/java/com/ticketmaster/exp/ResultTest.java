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

package com.ticketmaster.exp;

import com.ticketmaster.exp.util.SameWhens;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static org.junit.Assert.assertEquals;

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
        control, candidate
    );

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
    Result<String> result = new Result<>("exp", Instant.now(), controlTrialResult, candidateTrialResult);

    // WHEN
    MatchType matchType = result.determineMatch(Object::equals, Objects::equals);

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
    Result<String> result = new Result<>("exp", Instant.now(), controlTrialResult, candidateTrialResult);

    // WHEN
    MatchType matchType = result.determineMatch(Object::equals, Objects::equals);

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
    Result<String> result = new Result<>("exp", Instant.now(), controlTrialResult, candidateTrialResult);

    // WHEN
    MatchType matchType = result.determineMatch(Object::equals, Objects::equals);

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
    Result<String> result = new Result<>("exp", Instant.now(), controlTrialResult, candidateTrialResult);

    // WHEN
    MatchType matchType = result.determineMatch(Object::equals, Objects::equals);

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
    Result<String> result = new Result<>("exp", Instant.now(), controlTrialResult, candidateTrialResult);

    // WHEN
    MatchType matchType = result.determineMatch(Object::equals, SameWhens.classesMatch());

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
    Result<String> result = new Result<>("exp", Instant.now(), controlTrialResult, candidateTrialResult);

    // WHEN
    MatchType matchType = result.determineMatch(Object::equals, Objects::equals);

    // THEN
    assertEquals(MatchType.EXCEPTION_MISMATCH, matchType);

  }
}