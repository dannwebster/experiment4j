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

import static org.junit.Assert.assertEquals;

public class ConcurrentMapMatchCounterTest {

  @Test
  public void testGetAndIncrementOnEmptyMap() throws Exception {

    // GIVEN
    ConcurrentMapMatchCounter<String> ccmc = new ConcurrentMapMatchCounter<>();
    assertEquals(true, ccmc.getCounts().isEmpty());

    // WHEN
    int count = ccmc.getAndIncrement("foo", 1);

    // THEN
    assertEquals(1, count);
    assertEquals(1, ccmc.getMatchCount("foo").intValue());
    assertEquals(1, ccmc.getCounts().size());
    assertEquals(true, ccmc.getCounts().containsKey("foo"));
  }

  @Test
  public void testGetAndIncrementByTwoOnEmptyMap() throws Exception {

    // GIVEN
    ConcurrentMapMatchCounter<String> ccmc = new ConcurrentMapMatchCounter<>();
    assertEquals(true, ccmc.getCounts().isEmpty());

    // WHEN
    int count = ccmc.getAndIncrement("foo", 2);

    // THEN
    assertEquals(2, count);
    assertEquals(2, ccmc.getMatchCount("foo").intValue());
    assertEquals(1, ccmc.getCounts().size());
    assertEquals(true, ccmc.getCounts().containsKey("foo"));
  }

  @Test
  public void testGetAndIncrementOnExistingValue() throws Exception {

    // GIVEN
    ConcurrentMapMatchCounter<String> ccmc = new ConcurrentMapMatchCounter<>();
    assertEquals(true, ccmc.getCounts().isEmpty());
    int count = ccmc.getAndIncrement("foo", 1);

    // WHEN
    count = ccmc.getAndIncrement("foo", 1);

    // THEN
    assertEquals(2, count);
    assertEquals(2, ccmc.getMatchCount("foo").intValue());
    assertEquals(1, ccmc.getCounts().size());
    assertEquals(true, ccmc.getCounts().containsKey("foo"));
  }

  @Test
  public void testGetAndIncrementByTwoOnExistingValue() throws Exception {

    // GIVEN
    ConcurrentMapMatchCounter<String> ccmc = new ConcurrentMapMatchCounter<>();
    assertEquals(true, ccmc.getCounts().isEmpty());
    int count = ccmc.getAndIncrement("foo", 1);

    // WHEN
    count = ccmc.getAndIncrement("foo", 2);

    // THEN
    assertEquals(3, count);
    assertEquals(3, ccmc.getMatchCount("foo").intValue());
    assertEquals(1, ccmc.getCounts().size());
    assertEquals(true, ccmc.getCounts().containsKey("foo"));
  }

}