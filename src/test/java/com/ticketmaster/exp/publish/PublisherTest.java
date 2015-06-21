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
import com.ticketmaster.exp.TrialResult;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PublisherTest {
  @Test
  public void testPublisherSyntax() throws Exception {
    // GIVEN
    Measurer<String> m = mock(Measurer.class);

    DurationNamer<String> dn = PatternDurationNamer.DEFAULT;
    MatchCountNamer<String> mc = PatternMatchCountNamer.DEFAULT;


    Duration candidateD = Duration.ofSeconds(5);
    Duration controlD = Duration.ofSeconds(10);
    Result<String> result = new Result(
        "experiment",
        Instant.now(),
        new TrialResult(CONTROL, controlD, null, "bar"), new TrialResult(CANDIDATE, candidateD, null, "foo")
    );

    // WHEN
    Publisher publisher = MeasurerPublisher.builder()
        .matchCountNamer(mc)
        .durationNamer(dn)
        .measurer(m)
        .build();

    publisher.publish(MatchType.MATCH, result);

    // THEN
    verify(m, times(1)).measureCount("exp.experiment.match.type.match.count", 1);
    verify(m, times(1)).measureDuration("exp.experiment.trial.type.candidate.dur", candidateD);
    verify(m, times(1)).measureDuration("exp.experiment.trial.type.control.dur", controlD);
  }

}
