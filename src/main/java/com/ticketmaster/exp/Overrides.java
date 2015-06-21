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

import com.ticketmaster.exp.util.Try;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class Overrides<O> {
  private BooleanSupplier doExperimentWhen;
  private Function<Result<O>, Try<O>> returnChoice;

  Overrides() {}

  public static <O> Overrides<O>  overrides() {
    return new Overrides<>();
  }

  public Overrides<O> returnChoice(Function<Result<O>, Try<O>> returnChoice) {
    this.returnChoice = returnChoice;
    return this;
  }

  public Overrides<O> doExperimentWhen(BooleanSupplier doExperimentWhen) {
    this.doExperimentWhen = doExperimentWhen;
    return this;
  }

  public Optional<Function<Result<O>, Try<O>>> optionalReturnChoice() {
    return Optional.ofNullable(returnChoice);
  }

  public Optional<BooleanSupplier> optionalDoExperimentWhen() {
    return Optional.ofNullable(doExperimentWhen);
  }
}
