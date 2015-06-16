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

/**
 * Created by dannwebster on 4/18/15.
 */
public class PrintStreamMeasurer implements Measurer<String> {
  public static final PrintStreamMeasurer DEFAULT = from(
      System.out,
      new ConcurrentMapMatchCounter<String>());

  private final PrintStream printStream;
  private final MatchCounter<String> matchCounter;

  public static PrintStreamMeasurer from(PrintStream printStream, MatchCounter<String> matchCounter) {
    return new PrintStreamMeasurer(printStream, matchCounter);
  }

  private PrintStreamMeasurer(PrintStream printStream, MatchCounter<String> matchCounter) {
    Assert.notNull(printStream, "printStream must be non-null");
    Assert.notNull(matchCounter, "matchCounter must be non-null");

    this.printStream = printStream;
    this.matchCounter = matchCounter;
  }

  @Override
  public void measureDuration(String metricKey, Duration duration) {
    printStream.println(metricKey + "=" + duration.toMillis());
  }

  @Override
  public void measureCount(String metricKey, int count) {
    int matchCount = matchCounter.getAndIncrement(metricKey);
    printStream.println(metricKey + "=" + matchCount);
  }
}
