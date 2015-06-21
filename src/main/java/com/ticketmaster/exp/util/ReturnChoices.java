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
import com.ticketmaster.exp.TrialType;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class ReturnChoices {
  ReturnChoices() {
  }

  public static <T> Function<Result<T>, Try<T>> ofType(TrialType type) {
    return (result) -> TrialType.CANDIDATE.equals(type) ?
        result.getCandidateResult().getTryResult() :
        result.getControlResult().getTryResult();
  }


  public static <T> Function<Result<T>, Try<T>> candidateWhen(BooleanSupplier candidateWhen) {
    return (result) -> (candidateWhen.getAsBoolean()) ?
        result.getCandidateResult().getTryResult() :
        result.getControlResult().getTryResult();
  }

  public static <T> Function<Result<T>, Try<T>> alwaysControl() {
    return ofType(TrialType.CONTROL);
  }

  public static <T> Function<Result<T>, Try<T>> alwaysCandidate() {
    return ofType(TrialType.CANDIDATE);
  }

  public static <T> Function<Result<T>, Try<T>> findFastest() {
    return (result) -> result.getControlResult().getDuration()
          .compareTo(result.getCandidateResult().getDuration()) <= 0
        ?
        result.getControlResult().getTryResult() :
        result.getCandidateResult().getTryResult();

  }

  public static <T> Function<Result<T>, Try<T>> findBest() {
    Function<Result<T>, Try<T>> fastest = findFastest();
    return (result) -> result.getControlResult().getTryResult().isFailure() ?
        result.getCandidateResult().getTryResult() :
        result.getCandidateResult().getTryResult().isFailure() ?
            result.getControlResult().getTryResult() :
            fastest.apply(result);
  }
}
