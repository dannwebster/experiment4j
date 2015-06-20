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

import com.ticketmaster.exp.util.ReturnChoices;
import com.ticketmaster.exp.util.SameWhens;
import com.ticketmaster.exp.util.Selectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

import static com.ticketmaster.exp.util.Selectors.always;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by dannwebster on 10/12/14.
 */

public class TrialTest {
  public static final String ARGS = "foo";

  Function<String, String> candidate = mock(Function.class);
  Function<String, String> control = mock(Function.class);
  Publisher<String> p = mock(Publisher.class);
  Clock c = mock(Clock.class);

  @Before
  public void setUp() throws Exception {
    when(c.instant()).thenAnswer((inv) -> Instant.EPOCH);
    when(candidate.apply(any())).thenReturn("candidate");
    when(control.apply(any())).thenReturn("control");
  }

  @Test
  public void testSimpleCallsPublish() throws Exception {

    // GIVEN
    Function<String, String> e = Trial.<String, String>simple("my simple experiment")
        .control(control)
        .candidate(candidate)
        .timedBy(c)
        .publishedBy(p)
        .get();

    // WHEN
    String s = e.apply(ARGS);

    // THEN
    assertEquals("control", s);
    verify(p, times(1)).publish(Matchers.eq(MatchType.MISMATCH), Matchers.any(Result.class));
    verify(candidate, times(1)).apply(ARGS);
    verify(control, times(1)).apply(ARGS);
  }

  @Test
  public void testExperimentCallsPublishWithMatch() throws Exception {

    // GIVEN
    when(candidate.apply(any())).thenReturn("control");
    Trial<String, String, String> e = Trial.<String, String, String>named("my experiment")
        .control(control)
        .candidate(candidate)
        .timedBy(c)
        .simplifiedBy(a -> a)
        .publishedBy(p)
        .get();

    // WHEN
    String s = e.apply(ARGS);

    // THEN
    assertEquals("control", s);
    verify(p, times(1)).publish(Matchers.eq(MatchType.MATCH), Matchers.any(Result.class));
    verify(candidate, times(1)).apply(ARGS);
    verify(control, times(1)).apply(ARGS);
  }

  @Test
  public void testReturnCandidate() throws Exception {

    // GIVEN
    Trial<String, String, String> e = Trial.<String, String, String>named("my experiment")
        .control(control)
        .candidate(candidate)
        .timedBy(c)
        .simplifiedBy(a -> a)
        .returnChoice(ReturnChoices.alwaysCandidate())
        .publishedBy(p)
        .get();

    // WHEN
    String s = e.apply(ARGS);

    // THEN
    assertEquals("candidate", s);
    verify(p, times(1)).publish(Matchers.eq(MatchType.MISMATCH), Matchers.any(Result.class));
    verify(candidate, times(1)).apply(ARGS);
    verify(control, times(1)).apply(ARGS);
  }

  @Test
  public void testIgnoreExperiment() throws Exception {

    // GIVEN
    Trial<String, String, String> e = Trial.<String, String, String>named("my experiment")
        .control(control)
        .candidate(candidate)
        .timedBy(c)
        .simplifiedBy(a -> a)
        .doExperimentWhen(Selectors.never())
        .publishedBy(p)
        .get();

    // WHEN
    String s = e.apply(ARGS);

    // THEN
    assertEquals("control", s);
    verify(control, times(1)).apply(ARGS);
    verify(candidate, never()).apply(ARGS);
    verify(p, never()).publish(any(), any());
  }

  @Rule
  public ExpectedException ex = ExpectedException.none();

  @Test
  public void testFailsOnControlFailure() throws Exception {

    // GIVEN
    when(control.apply(ARGS)).thenThrow(new IllegalArgumentException("control failed"));

    Trial<String, String, String> e = Trial.<String, String, String>named("my experiment")
        .control(control)
        .candidate(candidate)
        .timedBy(c)
        .simplifiedBy(a -> a)
        .doExperimentWhen(Selectors.never())
        .publishedBy(p)
        .get();

    // EXPECT
    ex.expect(IllegalArgumentException.class);
    ex.expectMessage("control failed");

    // WHEN
    String s = e.apply(ARGS);
  }

  @Test
  public void testSucceedsOnCandiateFailure() throws Exception {

    // GIVEN
    when(candidate.apply(ARGS)).thenThrow(new IllegalArgumentException("control failed"));

    Trial<String, String, String> e = Trial.<String, String, String>named("my experiment")
        .control(control)
        .candidate(candidate)
        .simplifiedBy(a -> a)
        .doExperimentWhen(Selectors.never())
        .sameWhen(Objects::equals)
        .exceptionsSameWhen(SameWhens.classesMatch())
        .publishedBy(p)
        .get();

    // WHEN
    String s = e.apply(ARGS);

    // THEN
    assertEquals("control", s);
    verify(control, times(1)).apply(ARGS);
    verify(candidate, never()).apply(ARGS);
    verify(p, never()).publish(any(), any());
  }

  @Test
  public void testExperimentsShouldPerformMethodsSeriallyWhenSerialDoSeriallyWhenReturnsTrue() throws Exception {

    // GIVEN
    Trial<String, String, String> e = Trial.<String, String>simple("my experiment")
        .control(control)
        .candidate(candidate)
        .doSeriallyWhen(always())
        .publishedBy(p)
        .get();

    // WHEN

    // THEN


  }
}
