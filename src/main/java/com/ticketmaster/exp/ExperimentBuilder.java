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

import java.time.Clock;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by dannwebster on 6/20/15.
 */
public interface ExperimentBuilder<I, O, M, E extends Trial<I, O, M>, B extends ExperimentBuilder<I, O, M, E, B>>
    extends Supplier<Trial<I, O, M>> {

  public Trial<I, O, M> get();

  public String getName();

  public B simplifiedBy(Function<O, M> simplifier);

  public B sameWhen(BiFunction<M, M, Boolean> sameWhen);

  public B exceptionsSameWhen(BiFunction<Exception, Exception, Boolean> sameWhen);

  public B timedBy(Clock clock);

  public B publishedBy(Publisher<O> publisher);

  public B returnChoice(Function<Result<O>, Try<O>> returnChoice);

  public B doExperimentWhen(BooleanSupplier doExperimentWhen);

  public B doSeriallyWhen(BooleanSupplier doSeriallyWhen);

  public B control(Function<I, O> control);

  public B candidate(Function<I, O> candidate);
}
