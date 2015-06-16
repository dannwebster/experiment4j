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

import com.ticketmaster.exp.MatchType;

import java.util.Locale;

/**
 * Created by dannwebster on 4/18/15.
 */
public class PatternMatchCountNamer implements MatchCountNamer<String> {
  public static final String DEFAULT_PATTERN = "exp.%s.match.type.%s.count";
  public static final PatternMatchCountNamer DEFAULT = from(DEFAULT_PATTERN);
  private final String pattern;

  public static PatternMatchCountNamer from(String pattern) {
    return new PatternMatchCountNamer(pattern);
  }

  private PatternMatchCountNamer(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public String name(String experimentName, MatchType matchType) {
    return String.format(pattern, experimentName, matchType.name().toLowerCase(Locale.getDefault()));
  }
}
