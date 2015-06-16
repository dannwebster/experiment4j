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

import java.time.Duration;

/**
 * Created by dannwebster on 10/12/14.
 */
public class TrialResult<T> {
  private final TrialType trialType;
  private final Duration duration;
  private final Try<T> result;

  public TrialResult(TrialType trialType, Duration duration, Exception exception, T value) {
    Assert.notNull(trialType, "trialType must be non-null");
    Assert.notNull(duration, "duration must be non-null");
    this.trialType = trialType;
    this.duration = duration;
    this.result = Try.of(value, exception);
  }

  public TrialType getTrialType() {
    return trialType;
  }

  public Duration getDuration() {
    return duration;
  }

  public Try<T> getTryResult() {
    return result;
  }
}
