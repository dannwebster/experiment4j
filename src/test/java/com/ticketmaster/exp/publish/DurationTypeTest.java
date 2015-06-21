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

import static com.ticketmaster.exp.publish.DurationNamer.DurationType;
import static org.junit.Assert.assertEquals;

public class DurationTypeTest {
  @Test
  public void testValueOf() throws Exception {
    // EXPECT
    assertEquals(DurationType.CANDIDATE, DurationType.valueOf("CANDIDATE"));
    assertEquals(DurationType.CONTROL, DurationType.valueOf("CONTROL"));
    assertEquals(DurationType.IMPROVEMENT, DurationType.valueOf("IMPROVEMENT"));
    assertEquals(3, DurationType.values().length);


  }

}