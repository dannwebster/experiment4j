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

import com.ticketmaster.exp.MatchType;
import com.ticketmaster.exp.Publisher;
import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.util.Assert;

import java.time.Duration;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static com.ticketmaster.exp.TrialType.IMPROVEMENT;

/**
 * Created by dannwebster on 10/12/14.
 */
public class MeasurerPublisher<K> implements Publisher {
  public static final MeasurerPublisher<String> DEFAULT = MeasurerPublisher.<String>builder()
      .durationNamer(PatternDurationNamer.DEFAULT)
      .matchCountNamer(PatternMatchCountNamer.DEFAULT)
      .measurer(PrintStreamMeasurer.DEFAULT)
      .build();

  private final Measurer measurer;
  private final MatchCountNamer matchCountNamer;
  private final DurationNamer durationNamer;

  public static class Builder<K> {
    private Measurer measurer = PrintStreamMeasurer.DEFAULT;
    private MatchCountNamer<K> matchCountNamer;
    private DurationNamer<K> durationNamer;

    private Builder() {
    }

    public MeasurerPublisher<K> build() {
      return new MeasurerPublisher<K>(measurer, matchCountNamer, durationNamer);
    }

    public Builder<K> measurer(Measurer measurer) {
      this.measurer = measurer;
      return this;
    }

    public Builder<K> matchCountNamer(MatchCountNamer matchCountNamer) {
      this.matchCountNamer = matchCountNamer;
      return this;
    }

    public Builder<K> durationNamer(DurationNamer durationNamer) {
      this.durationNamer = durationNamer;
      return this;
    }

  }

  public static <K> Builder<K> builder() {
    return new Builder<>();
  }

  private MeasurerPublisher(Measurer<K> measurer,
                            MatchCountNamer<K> matchCountNamer,
                            DurationNamer<K> durationNamer) {

    Assert.notNull(measurer, "measurer must be non-null");
    Assert.notNull(matchCountNamer, "matchCountNamer must be non-null");
    Assert.notNull(durationNamer, "durationNamer must be non-null");
    this.measurer = measurer;
    this.matchCountNamer = matchCountNamer;
    this.durationNamer = durationNamer;
  }

  @Override
  public void publish(MatchType matchType, Result payload) {
    String name = payload.getName();
    measurer.measureCount(matchCountNamer.name(name, matchType), 1);
    Duration controlDuration = payload.getControlResult().getDuration();
    Duration candidateDuration = payload.getCandidateResult().getDuration();
    Duration difference = controlDuration.minus(candidateDuration);
    measurer.measureDuration(durationNamer.name(name, CONTROL), controlDuration);
    measurer.measureDuration(durationNamer.name(name, CANDIDATE), candidateDuration);
    measurer.measureDuration(durationNamer.name(name, IMPROVEMENT), difference);
  }
}
