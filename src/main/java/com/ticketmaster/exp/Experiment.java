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

import com.ticketmaster.exp.publish.MeasurerPublisher;
import com.ticketmaster.exp.util.ReturnChoices;
import com.ticketmaster.exp.util.SameWhens;
import com.ticketmaster.exp.util.Try;

import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import static com.ticketmaster.exp.util.Selectors.always;

public class Experiment<I, O> {
  private String name;
  private Function<I, O> control;
  private Function<I, O> candidate;
  // one of these will exist PER EXPERIMENT, and will have 2 threads associated with
  // running the trial: one for the candidate, one for the control
  private ExecutorService executorService = Executors.newFixedThreadPool(2);
  private Function<Result<O>, Try<O>> returnChoice = ReturnChoices.alwaysControl();
  private BooleanSupplier doExperimentWhen = always();
  private BiFunction<O, O, Boolean> sameWhen = Objects::equals;
  private BiFunction<Exception, Exception, Boolean> exceptionsSameWhen = SameWhens.classesMatch();
  private Publisher<O> publisher = MeasurerPublisher.DEFAULT;
  private Clock clock = Clock.systemUTC();

  public Trial<I, O> trialWithOverrides(Overrides<O> overrides) {
    return trial().override(overrides);
  }

  public Trial<I, O> trial() {
    return new Trial<>(
        this.name,
        this.control,
        this.candidate,
        this.executorService,
        this.returnChoice,
        this.doExperimentWhen,
        this.sameWhen,
        this.exceptionsSameWhen,
        this.publisher,
        this.clock);
  }

  public static <I, O> Experiment<I, O> named(String name) {
    return new Experiment<I, O>(name);
  }

  public Experiment(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public Experiment<I, O> sameWhen(BiFunction<O, O, Boolean> sameWhen) {
    this.sameWhen = sameWhen;
    return this;
  }

  public Experiment<I, O> exceptionsSameWhen(BiFunction<Exception, Exception, Boolean> exceptionsSameWhen) {
    this.exceptionsSameWhen = exceptionsSameWhen;
    return this;
  }

  public Experiment<I, O> timedBy(Clock clock) {
    this.clock = clock;
    return this;
  }

  public Experiment<I, O> publishedBy(Publisher<O> publisher) {
    this.publisher = publisher;
    return this;
  }

  public Experiment<I, O> returnChoice(Function<Result<O>, Try<O>> returnChoice) {
    this.returnChoice = returnChoice;
    return this;
  }

  public Experiment<I, O> doExperimentWhen(BooleanSupplier doExperimentWhen) {
    this.doExperimentWhen = doExperimentWhen;
    return this;
  }

  public Experiment<I, O> control(Function<I, O> control) {
    this.control = control;
    return this;
  }

  public Experiment<I, O> candidate(Function<I, O> candidate) {
    this.candidate = candidate;
    return this;
  }

  public Experiment<I, O> withExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
    return this;
  }

}
