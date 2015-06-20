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
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScienceTest {
  Supplier callTracker = mock(Supplier.class);
  public static final String ARGS = "args";

  @Before
  public void setUp() throws Exception {
    assertEquals(0, Science.science().getExperimentCount());
  }

  @After
  public void tearDown() throws Exception {
    Science.science().getClearExperiments();
    assertEquals(0, Science.science().getExperimentCount());
  }

  public Trial<String, String, String> buildExperiment(Supplier callTracker) throws Exception {
    return Science.science().experiment(
        "my-experiment",
        () -> {
          callTracker.get();
          return Trial
              .simple("my-experiment")
              .control((args) -> "foo")
              .candidate((args) -> "candidate")
              .get();
        }
    );
  }

  @Test
  public void testDoExperimentDoesNotRebuildExperiment() throws Exception {
    Trial<String, String, String> exp = buildExperiment(callTracker);

    // GIVEN
    Science.science().experiment(
        "my-experiment",
        () -> {
          callTracker.get();
          return Trial
              .named("my-experiment")
              .control((args) -> "foo")
              .candidate((args) -> "candidate").get();
        }
    ).apply(new Object[]{});

    // WHEN
    String str = Science.science().doExperiment("my-experiment", new Object[]{});


    // THEN
    assertEquals("foo", str);
    verify(callTracker, times(1)).get();

  }

  @Test
  public void testRepeatedExperimentCallDoesNotRebuildExperiment() throws Exception {
    // GIVEN
    assertEquals("foo", buildExperiment(callTracker).apply(ARGS));

    // WHEN
    assertEquals("foo", buildExperiment(callTracker).apply(ARGS));

    // THEN
    verify(callTracker, times(1)).get();
  }

  @Test
  public void testGetExperimentShouldNotReturnExperimentWhenNotBuilt() throws Exception {
    // GIVEN
    // no build experiment

    // WHEN
    Optional<Trial<String, String, String>> optEx = Science.science().getExperiment("my-experiment");

    // THEN
    assertEquals(false, optEx.isPresent());

  }

  @Test
  public void testGetExperimentShouldReturnExperimentWhenBuilt() throws Exception {
    // GIVEN
    buildExperiment(callTracker);

    // WHEN
    Optional<Trial<String, String, String>> optEx = Science.science().getExperiment("my-experiment");

    // THEN
    assertEquals(true, optEx.isPresent());

  }

  @Test
  public void testExperimentsShouldContainExperimentWhenExperimentsBuilt() throws Exception {
    // GIVEN
    assertEquals(true, Science.science().experiments().isEmpty());
    buildExperiment(callTracker);

    // WHEN
    Map<String, Trial> experimentMap = Science.science().experiments();

    // THEN
    assertEquals(1, experimentMap.size());
    assertNotNull(experimentMap.get("my-experiment"));
  }

  @Ignore
  @Test
  public void testDoExperimentWithNullReturnShouldReturnNull() throws Exception {

    // GIVEN
    Science.science().experiment(
        "my-experiment",
        Trial
            .<String, String>simple("my-experiment")
            .control((args) -> (String) null)
            .candidate((args) -> "candidate")
    );

    // WHEN
    String s = Science.science().doExperiment("my-experiment", ARGS);

    // THEN
    assertNull(s);
  }
}