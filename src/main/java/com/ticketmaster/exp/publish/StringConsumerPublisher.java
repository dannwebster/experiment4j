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
import com.ticketmaster.exp.Publisher;
import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.util.Assert;

import java.util.function.Consumer;

/**
 * Created by dannwebster on 6/14/15.
 */
public class StringConsumerPublisher<T> implements Publisher<T> {

  private final Consumer<String> stringConsumer;

  public StringConsumerPublisher(Consumer<String> stringConsumer) {
    Assert.notNull(stringConsumer, "stringConsumer must be non-null");
    this.stringConsumer = stringConsumer;
  }

  @Override
  public void publish(MatchType matchType, Result<T> payload) {
    stringConsumer.accept("candidate took "
        + payload.getCandidateResult().getDuration().toMillis() + " millis to execute");

    stringConsumer.accept("control took "
        + payload.getControlResult().getDuration().toMillis() + " millis to execute");

    String message = "";
    switch (matchType) {
      case MATCH:
        message = "candidate and control both executed successfully and match";
        break;
      case EXCEPTION_MATCH:
        message = "candidate and control both threw exceptions, and the exceptions match";
        break;
      case MISMATCH:
        message = "candidate and control both executed successfully, but the responses don't match";
        break;
      case EXCEPTION_MISMATCH:
        message = "candidate and control both threw exceptions, but the exceptions don't match";
        break;
      case CONTROL_EXCEPTION:
        message = "the candidate executed successfully but the control threw an exception";
        break;
      case CANDIDATE_EXCEPTION:
        message = "the control executed successfully but the candidate threw an exception";
        break;
    }
    stringConsumer.accept(payload.getName() + ": " + message);
  }
}
