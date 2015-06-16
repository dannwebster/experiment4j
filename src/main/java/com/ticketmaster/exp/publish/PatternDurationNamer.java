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

import com.ticketmaster.exp.TrialType;

import java.util.Locale;

/**
 * Created by dannwebster on 4/18/15.
 */
public class PatternDurationNamer implements DurationNamer<String> {
  public static final String DEFAULT_PATTERN = "exp.%s.trial.type.%s.dur";
  public static final PatternDurationNamer DEFAULT = from(DEFAULT_PATTERN);

  private final String pattern;

  public static PatternDurationNamer from(String pattern) {
    return new PatternDurationNamer(pattern);
  }

  private PatternDurationNamer(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public String name(String experimentName, TrialType trialType) {
    return String.format(pattern, experimentName,
        trialType.name().toLowerCase(Locale.getDefault()));
  }
}
