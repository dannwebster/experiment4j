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

package com.ticketmaster.exp.util;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class DupleTest {

  @Test
  public void testFrom() throws Exception {
    // GIVEN
    Duple<String> d = Duple.from("s1", "s2");

    // THEN
    assertEquals("s1", d.getElement1());
    assertEquals("s2", d.getElement2());
    Iterator<String> i = d.stream().iterator();

    assertEquals("s1", i.next());
    assertEquals("s2", i.next());
    assertEquals(false, i.hasNext());

    i = d.parallelStream().iterator();
    i.next();
    i.next();
    assertEquals(false, i.hasNext());

  }
}