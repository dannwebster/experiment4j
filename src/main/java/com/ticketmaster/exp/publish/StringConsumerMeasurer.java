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

package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.util.Assert;

import java.io.PrintStream;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * Created by dannwebster on 4/18/15.
 */
public class StringConsumerMeasurer implements Measurer<String> {

  private final Consumer<String> stringConsumer;
  private final MatchCounter<String> matchCounter;

  public StringConsumerMeasurer(Consumer<String> stringConsumer, MatchCounter<String> matchCounter) {
    Assert.notNull(stringConsumer, "stringConsumer must be non-null");
    Assert.notNull(matchCounter, "matchCounter must be non-null");

    this.stringConsumer = stringConsumer;
    this.matchCounter = matchCounter;
  }

  @Override
  public void measureDuration(String metricKey, Duration duration) {
    stringConsumer.accept(metricKey + "=" + duration.toMillis());
  }

  @Override
  public void measureCount(String metricKey, int count) {
    int matchCount = matchCounter.getAndIncrement(metricKey, count);
    stringConsumer.accept(metricKey + "=" + matchCount);
  }
}
