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

import java.util.Comparator;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

public class SameWhensTest {
  // remove coverage noise
  SameWhens sameWhens = new SameWhens();

  @Test
  public void testForComparator() throws Exception {

    // GIVEN
    Comparator<String> comp = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    };

    // WHEN
    BiFunction<String, String, Boolean> biFunction = SameWhens.fromComparator(comp);

    // THEN
    assertEquals(true, biFunction.apply("foo", "foo"));
    assertEquals(false, biFunction.apply("foo", "bar"));
  }

  @Test
  public void testSameClass() throws Exception {

    assertEquals(true, SameWhens.classesMatch().apply("foo", "bar"));
    assertEquals(false, SameWhens.classesMatch().apply("foo", new Integer(1)));
    assertEquals(false, SameWhens.classesMatch().apply(null, new Integer(1)));
    assertEquals(false, SameWhens.classesMatch().apply("foo", null));


  }
}
