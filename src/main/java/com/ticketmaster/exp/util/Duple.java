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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Duple<E> {
  private final E element1;
  private final E element2;
  private final List<E> elementList;

  public static <E> Duple<E> from(E element1, E element2) {
    return new Duple<>(element1, element2);
  }

  private Duple(E element1, E element2) {
    Assert.notNull(element1, "first element must be non null");
    Assert.notNull(element2, "second element must be non null");
    this.element1 = element1;
    this.element2 = element2;
    elementList = Arrays.asList(element1, element2);
  }

  public Stream<E> parallelStream() {
    return elementList.parallelStream();
  }

  public Stream<E> stream() {
    return elementList.stream();
  }

  public E getElement1() {
    return element1;
  }

  public E getElement2() {
    return element2;
  }
}
