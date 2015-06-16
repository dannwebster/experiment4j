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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Created by dannwebster on 4/18/15.
 */
public class Science {
  private final Map<String, Experiment> cache = new ConcurrentHashMap<>();

  public static final Science INSTANCE = new Science();

  private Science() {
  }

  public static Science science() {
    return INSTANCE;
  }

  public <I, O> O doExperiment(String name, I args) throws Exception {
    return (O) getExperiment(name)
        .orElseGet(() -> null)
        .apply(args);
  }

  public <I, O, M> Experiment<I, O, M> experiment(
      String name, Supplier<? extends Experiment> experimentBuilder) {
    return cache.computeIfAbsent(name, (key) -> experimentBuilder.get());
  }

  public <I, O, M> Optional<Experiment<I, O, M>> getExperiment(String name) {
    return Optional.ofNullable(cache.get(name));
  }

  public Map<String, Experiment> experiments() {
    return Collections.unmodifiableMap(cache);
  }

  public void getClearExperiments() {
    cache.clear();
  }

  public int getExperimentCount() {
    return cache.size();
  }
}
