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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Science {
  private final Map<String, Experiment> cache = new ConcurrentHashMap<>();

  public static final Science INSTANCE = new Science();

  private Science() {
  }

  public static Science science() {
    return INSTANCE;
  }

  public <I, O> Experiment<I, O> experiment(String name, Supplier<Experiment<I, O>> experimentSupplier) {
    return cache.computeIfAbsent(name, (key) -> experimentSupplier.get() );
  }


  public Map<String, Experiment> experiments() {
    return Collections.unmodifiableMap(cache);
  }

  public void clearExperiments() {
    cache.clear();
  }

  public int getExperimentCount() {
    return cache.size();
  }
}
