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

import com.ticketmaster.exp.util.Assert;

import java.time.Instant;
import java.util.function.BiFunction;

import static com.ticketmaster.exp.MatchType.CANDIDATE_EXCEPTION;
import static com.ticketmaster.exp.MatchType.CONTROL_EXCEPTION;
import static com.ticketmaster.exp.MatchType.EXCEPTION_MATCH;
import static com.ticketmaster.exp.MatchType.EXCEPTION_MISMATCH;
import static com.ticketmaster.exp.MatchType.MATCH;
import static com.ticketmaster.exp.MatchType.MISMATCH;

public class Result<T> {

  private final String name;
  private final Instant timestamp;
  private final TrialResult<T> controlResult;
  private final TrialResult<T> candidateResult;

  public Result(String name,
                Instant timestamp,
                TrialResult<T> controlResult,
                TrialResult<T> candidateResult) {

    Assert.hasText(name, "name must be non-empty");
    Assert.notNull(timestamp, "timestamp must be non-null");
    Assert.notNull(controlResult, "controlResult must be non-null");
    Assert.notNull(candidateResult, "candidateResult must be non-null");
    this.name = name;
    this.timestamp = timestamp;
    this.controlResult = controlResult;
    this.candidateResult = candidateResult;
  }

  public MatchType determineMatch(BiFunction<T, T, Boolean> sameWhen,
                                      BiFunction<? super Exception, ? super Exception, Boolean>
                                          exceptionsSameWhen) {
    MatchType matchType;
    if (bothReturnedValues()) {
      matchType = valuesMatch(sameWhen) ? MATCH : MISMATCH;

    } else if (bothThrewExceptions()) {
      matchType = exceptionsMatch(exceptionsSameWhen) ? EXCEPTION_MATCH : EXCEPTION_MISMATCH;

    } else {
      matchType = controlThrewException() ? CONTROL_EXCEPTION : CANDIDATE_EXCEPTION;
    }
    return matchType;
  }

  boolean controlThrewException() {
    return controlResult.getTryResult().isFailure();
  }

  boolean bothReturnedValues() {
    return candidateResult.getTryResult().isSuccess() &&
        controlResult.getTryResult().isSuccess();
  }

  boolean bothThrewExceptions() {
    return candidateResult.getTryResult().isFailure() &&
        controlResult.getTryResult().isFailure();
  }

  boolean valuesMatch(BiFunction<T, T, Boolean> sameWhen) {
    T controlValue = controlResult.getTryResult().value().get();
    T candiateValue = candidateResult.getTryResult().value().get();
    return sameWhen.apply(controlValue, candiateValue);
  }

  boolean exceptionsMatch(
      BiFunction<? super Exception, ? super Exception, Boolean> exceptionsSameWhen) {
    Exception controlException = controlResult.getTryResult().exception().get();
    Exception candiateException = candidateResult.getTryResult().exception().get();
    return exceptionsSameWhen.apply(controlException, candiateException);
  }

  public String getName() {
    return name;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public TrialResult<T> getCandidateResult() {
    return candidateResult;
  }

  public TrialResult<T> getControlResult() {
    return controlResult;
  }
}
