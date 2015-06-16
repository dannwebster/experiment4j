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

public class Assert {
  Assert() {
  }

  /**
   *
   * @param value
   * @param minInclusive
   * @param maxExclusive
   */
  public static void between(int value, int minInclusive, int maxExclusive) {
    if (value < minInclusive || value >= maxExclusive) {
      throw new IllegalArgumentException(
          String.format("value %d is not between bounds of %d (inclusive) and %d (exclusive)",
              value, minInclusive, maxExclusive)
      );
    }
  }

  /**
   *
   * @param string
   * @param msg
   */
  public static void notNull(Object string, String msg) {
    if (string == null) {
      throw new IllegalArgumentException(msg);
    }
  }


  /**
   *
   * @param string
   * @param msg
   */
  public static void hasText(String string, String msg) {
    notNull(string, msg);
    if (string.isEmpty()) {
      throw new IllegalArgumentException(msg);
    }
  }

}
