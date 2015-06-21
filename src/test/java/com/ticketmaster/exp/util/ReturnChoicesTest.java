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

import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.TrialResult;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static org.junit.Assert.assertSame;

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
    Result<String> result = new Result<>("exp", instant, goodControl, goodCandidate);

    // WHEN
    Try<String> t = ReturnChoices.<String>alwaysControl().apply(result);

    // THEN
    assertSame("control", t.get());

  }

  @Test
  public void testAlwaysCandidateShouldAlwaysReturnCandidate() throws Exception {

    // GIVEN
    Result<String> result = new Result<>("exp", instant, goodControl, goodCandidate);

    // WHEN
    Try<String> t = ReturnChoices.<String>alwaysCandidate().apply(result);

    // THEN
    assertSame("candidate", t.get());
  }

  @Test
  public void testFindFastestShouldReturnFastest() throws Exception {

    // GIVEN
    Result<String> result = new Result<>("exp", instant, goodControl, goodCandidate);

    // WHEN
    Try<String> t = ReturnChoices.<String>findFastest().apply(result);

    // THEN
    // control duration is 100, candidate is 200
    assertSame("control", t.get());
  }


  @Test
  public void testFindFastestShouldReturnFastestCandidate() throws Exception {

    // GIVEN
    Result<String> result = new Result<>("exp", instant, goodControl, fasterGoodCandidate);

    // WHEN
    Try<String> t = ReturnChoices.<String>findFastest().apply(result);

    // THEN
    // control duration is 100, candidate is 200
    assertSame("candidate", t.get());
  }

  @Test
  public void testFindBestShouldReturnFastestWhenBothAreGood() throws Exception {

    // GIVEN
    Result<String> result = new Result<>("exp", instant, goodControl, goodCandidate);

    // WHEN
    Try<String> choice = ReturnChoices.<String>findBest().apply(result);

    // THEN
    assertSame("control", choice.get());
  }

  @Test
  public void testFindBestShouldReturnNonErrorCandidate() throws Exception {
    // GIVEN
    Result<String> badControlResult = new Result<>("exp", instant, badControl, goodCandidate);

    // WHEN
    Try<String> choice = ReturnChoices.<String>findBest().apply(badControlResult);

    // THEN
    assertSame("candidate", choice.get());
  }

  @Test
  public void testFindBestShouldReturnNonErrorControl() throws Exception {
    // GIVEN
    Result<String> badCandidateResult = new Result<>("exp", instant, goodControl, badCandidate);

    // WHEN
    Try<String> choice = ReturnChoices.<String>findBest().apply(badCandidateResult);

    // THEN
    assertSame("control", choice.get());
  }

  @Test
  public void testFindBestShouldControlWhenBothAreGoodAndSameSpeed() throws Exception {
    // GIVEN
    Result<String> badCandidateResult = new Result<>("exp", instant, goodControl, fastGoodCandidate);

    // WHEN
    Try<String> choice = ReturnChoices.<String>findBest().apply(badCandidateResult);

    // THEN
    assertSame("control", choice.get());
  }

  @Test
  public void testCandidateWhenReturnsCandidateWhenSelectorIsTrue() throws Exception {

    // GIVEN
    Result<String> result = new Result<>("exp", instant, goodControl, goodCandidate);

    // WHEN
    Try<String> choice = ReturnChoices.<String>candidateWhen(Selectors.always()).apply(result);

    // THEN
    assertSame("candidate", choice.get());
  }

  @Test
  public void testCandidateWhenReturnsControlWhenSelectorIsFalse() throws Exception {

    // GIVEN
    Result<String> result = new Result<>("exp", instant, goodControl, goodCandidate);

    // WHEN
    Try<String> choice = ReturnChoices.<String>candidateWhen(Selectors.never()).apply(result);

    // THEN
    assertSame("control", choice.get());
  }
}