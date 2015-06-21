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
import com.ticketmaster.exp.util.Try;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;

public class Trial<I, O> implements Function<I, O> {

  private final String name;
  private final Function<I, O> control;
  private final Function<I, O> candidate;
  private final ExecutorService executorService;
  private final Function<Result<O>, Try<O>> returnChoice;
  private final BooleanSupplier doExperimentWhen;
  private final Clock clock;

  private final BiFunction<O, O, Boolean> sameWhen;
  private final BiFunction<Exception, Exception, Boolean> exceptionsSameWhen;

  private final Publisher<O> publisher;

  public Trial(
      String name,
      Function<I, O> control,
      Function<I, O> candidate,
      ExecutorService executorService,
      Function<Result<O>, Try<O>> returnChoice,
      BooleanSupplier doExperimentWhen,
      BiFunction<O, O, Boolean> sameWhen,
      BiFunction<Exception, Exception, Boolean> exceptionsSameWhen,
      Publisher<O> publisher,
      Clock clock) {

    Assert.hasText(name, "name must be non-null and have text");
    Assert.notNull(control, "control must be non-null");
    Assert.notNull(candidate, "candidate must be non-null");
    Assert.notNull(executorService, "executorService must be non-null");
    Assert.notNull(returnChoice, "returnChoice must be non-null");
    Assert.notNull(doExperimentWhen, "doExperimentWhen must be non-null");
    Assert.notNull(sameWhen, "sameWhen must be non-null");
    Assert.notNull(exceptionsSameWhen, "exceptionsSameWhen must be non-null");
    Assert.notNull(publisher, "publisher must be non-null");
    Assert.notNull(clock, "clock must be non-null");

    this.name = name;
    this.control = control;
    this.candidate = candidate;
    this.executorService = executorService;
    this.returnChoice = returnChoice;
    this.doExperimentWhen = doExperimentWhen;
    this.sameWhen = sameWhen;
    this.exceptionsSameWhen = exceptionsSameWhen;
    this.publisher = publisher;
    this.clock = clock;
  }

  public Trial<I, O> override(Overrides<O> overrides) {
    return new Trial<>(
        this.name,
        this.control,
        this.candidate,
        this.executorService,
        overrides.optionalReturnChoice().orElse(this.returnChoice),
        overrides.optionalDoExperimentWhen().orElse(this.doExperimentWhen),
        this.sameWhen,
        this.exceptionsSameWhen,
        this.publisher,
        this.clock);
  }

  public final O apply(I args) {
    return perform(args).getOrThrowUnchecked();
  }

  final Try<O> perform(I args) {
    Result<O> result = getResult(args);
    Try<O> oTry = returnChoice.apply(result);
    return oTry;
  }

  final Result<O> getResult(I args) {
    Result<O> result;
    if (doExperimentWhen.getAsBoolean()) {
      result = runCandidateAndControl(args);

    } else {
      result = runOnlyControl(args);

    }
    return result;
  }

  Result<O> runOnlyControl(I args) {
    Instant timestamp = Instant.now();
    TrialResult<O> trialResult = observe(CONTROL, control, args);
    Result<O> result = new Result<>(
        name,
        timestamp,
        trialResult, trialResult
    );
    return result;
  }

  Result<O> runCandidateAndControl(I args) {
    Callable<TrialResult<O>> callableControl = () -> observe(CONTROL, control, args);
    Callable<TrialResult<O>> callableCandidate = () -> observe(CANDIDATE, candidate, args);

    Instant timestamp = Instant.now();
    Future<TrialResult<O>> controlFuture = executorService.submit(callableControl);
    Future<TrialResult<O>> candidateFuture = executorService.submit(callableCandidate);

    Result<O> result = null;
    try {
      result = new Result<>(
          name,
          timestamp,
          controlFuture.get(),
          candidateFuture.get());
    } catch (InterruptedException|ExecutionException e) {
      throw new RuntimeException(e);
    }

    MatchType matchType = determineMatch(result);
    publisher.publish(matchType, result);
    return result;
  }

  MatchType determineMatch(Result<O> result) {
    return result.determineMatch(this.sameWhen, this.exceptionsSameWhen);
  }

  final TrialResult<O> observe(TrialType trialType, Function<I, O> callable, I args) {
    Instant start = clock.instant();
    Exception exception = null;
    O value = null;
    try {
      value = callable.apply(args);
    } catch (Exception t) {
      exception = t;
    }
    Instant end = clock.instant();
    Duration duration = Duration.between(start, end);
    return new TrialResult<>(trialType, duration, exception, value);
  }

  public String getName() {
    return name;
  }
}
