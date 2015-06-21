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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static com.ticketmaster.exp.Science.science;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScienceTest {
  Supplier callTracker = mock(Supplier.class);
  public static final String ARGS = "args";

  @Before
  public void setUp() throws Exception {
    science().clearExperiments();
    assertEquals(0, science().getExperimentCount());
  }

  @After
  public void tearDown() throws Exception {
    science().clearExperiments();
    assertEquals(0, science().getExperimentCount());
  }

  @Test
  public void testExperimentCreatesExperimentsOnlyOnce() throws Exception {

    // GIVEN
    Supplier<Experiment<Object, Object>> experimentSupplier = mock(Supplier.class);
    when(experimentSupplier.get()).thenReturn(new Experiment("exp"));

    // WHEN
    for (int i = 0; i < 5; i++){
      science().experiment("exp", experimentSupplier);
    }

    // THEN
    assertEquals(1, science().getExperimentCount());
    // no matter how many times the experiment method was called, the supplier
    // is only used once
    verify(experimentSupplier, times(1)).get();

  }
}