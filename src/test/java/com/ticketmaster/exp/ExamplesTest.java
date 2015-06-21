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

import com.ticketmaster.exp.publish.PrintStreamPublisher;
import com.ticketmaster.exp.util.ReturnChoices;
import com.ticketmaster.exp.util.SameWhens;
import com.ticketmaster.exp.util.Selectors;
import org.junit.Test;

import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static com.ticketmaster.exp.Science.science;

public class ExamplesTest {
  public static class Person {
    private final String firstName;
    private final String lastName;

    public Person(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }
  }

  Function<Person, String> oldToName = (p) -> p.getFirstName() + " " + p.getLastName();
  Function<Person, String> newToName = (p) -> String.format("%s %s", p.getFirstName(), p.getLastName());

  Person[] people = {
      new Person("George", "Washington"),
      new Person("John", "Adams"),
      new Person("Thomas", "Jefferson"),
  };

  @Test
  public void showInlineSyntax() throws Exception {
    for (Person person : people) {
      inlineSyntax(person);
    }

  }

  /**
   * This syntax is designed to keep the configuration of the experiment as close as
   * possible to the execution of the experiment
   * @throws Exception
   */
  public void inlineSyntax(Person person) throws Exception {
    String personName = science()
        .experiment(
            "person-name",
            () -> Experiment.<Person, String>named("person-name")
                .control(oldToName)
                .candidate(newToName)
                .exceptionsSameWhen(SameWhens.messagesMatch())
                .sameWhen(Objects::equals)
                .doExperimentWhen(Selectors.always())
                .returnChoice(ReturnChoices.alwaysControl())
                .withExecutorService(Executors.newSingleThreadExecutor())
                .publishedBy(new PrintStreamPublisher<String>())
                .timedBy(Clock.systemUTC())
        )
        .trialWithOverrides(Overrides.<String>overrides()
                .doExperimentWhen(Selectors.percent(5))
                .returnChoice(ReturnChoices.candidateWhen(Selectors.percent(5)))
        )
        .apply(person);

  }
}
