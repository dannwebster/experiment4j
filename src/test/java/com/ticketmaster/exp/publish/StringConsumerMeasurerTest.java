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

import org.junit.Test;

import java.time.Duration;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StringConsumerMeasurerTest {

  @Test
  public void testCountShouldCallStringConsumer() throws Exception {
    // GIVEN
    Consumer<String> sc = mock(Consumer.class);
    MatchCounter<String> mc = mock(MatchCounter.class);
    when(mc.getAndIncrement("key.matches", 1)).thenReturn(1);
    StringConsumerMeasurer subject = new StringConsumerMeasurer(sc, mc);

    // WHEN
    subject.measureCount("key.matches", 1);

    // THEN
    verify(mc).getAndIncrement("key.matches", 1);
    verify(sc).accept("key.matches=1");

  }

  @Test
  public void testMeasureDurationShouldCallStringConsumer() throws Exception {
    // GIVEN
    Consumer<String> sc = mock(Consumer.class);
    MatchCounter<String> mc = mock(MatchCounter.class);
    StringConsumerMeasurer subject = new StringConsumerMeasurer(sc, mc);

    // WHEN
    subject.measureDuration("key.millis", Duration.ofMillis(5));

    // THEN
    verify(sc).accept("key.millis=5");
  }

}
