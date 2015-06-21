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

import com.ticketmaster.exp.util.Selectors;
import org.junit.Test;

import java.util.function.BooleanSupplier;

import static com.ticketmaster.exp.Science.science;
import static org.junit.Assert.assertEquals;

/**
 * Created by dannwebster on 6/20/15.
 */
public class ExperimentTest {
  @Test
  public void testExperiment() throws Exception {
    // GIVEN
    science().experiment("person-name", () -> new Experiment<Object, Object>("person-name"));

    // WHEN
    String name = science().experiments().get("person-name").getName();

    // THEN
    assertEquals("person-name", name);

  }
}
