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

import java.util.Comparator;
import java.util.function.BiFunction;

/**
 * Created by dannwebster on 4/19/15.
 */
public class SameWhens {
  SameWhens() {
  }

  public static <M> BiFunction<M, M, Boolean> classesMatch() {
    return (M m1, M m2) -> m1 != null && m2 != null && m1.getClass().equals(m2.getClass());
  }

  public static <M> BiFunction<M, M, Boolean> fromComparator(Comparator<M> comparator) {
    return (M m1, M m2) -> comparator.compare(m1, m2) == 0;
  }

}
