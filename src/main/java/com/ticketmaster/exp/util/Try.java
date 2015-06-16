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

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Created by dannwebster on 4/17/15.
 */
public class Try<T> implements Callable<T> {
  private final Optional<T> value;
  private final Optional<Exception> exception;

  public static <T> Try<T> from(Callable<T> callable) {
    T value = null;
    Exception exception = null;
    try {
      value = callable.call();
    } catch (Exception ex) {
      exception = ex;
    }
    return of(value, exception);

  }

  public static <T> Try<T> of(T value, Exception exception) {
    return new Try(value, exception);
  }

  private Try(T value, Exception exception) {
    if (!(value == null ^ exception == null)) {
      throw new IllegalArgumentException("exactly one of value or exception must be non-null");
    }
    this.value = Optional.ofNullable(value);
    this.exception = Optional.ofNullable(exception);
  }

  public T getOrThrow() throws Exception {
    return call();
  }

  public T getOrThrowUnchecked() {
    return get();
  }

  public T get() {
    try {
      return call();
    } catch (Exception e) {
      throw e instanceof RuntimeException
          ?
          (RuntimeException) e :
          new RuntimeException(e);
    }
  }

  public T call() throws Exception {
    if (value.isPresent()) {
      return value.get();
    } else {
      throw exception.get();
    }
  }

  public Optional<T> value() {
    return value;
  }

  public Optional<Exception> exception() {
    return exception;
  }

  public boolean isFailure() {
    return exception.isPresent();
  }

  public boolean isSuccess() {
    return value.isPresent();
  }

}
