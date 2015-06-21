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
import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.TrialResult;
import com.ticketmaster.exp.TrialType;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class PrintStreamPublisherTest {

  Result<String> result = new Result<>(
      "example",
      Instant.EPOCH,
      new TrialResult<>(TrialType.CONTROL, Duration.ofMillis(1L), null, "control"), new TrialResult<>(TrialType.CANDIDATE, Duration.ofMillis(1L), null, "candidate")
  );

  ByteArrayOutputStream bos = new ByteArrayOutputStream();
  PrintStream ps = new PrintStream(bos);

  @Test
  public void testSystemOutPublisher() throws Exception {

    // GIVEN
    PrintStreamPublisher<String> subject = new PrintStreamPublisher<>();

    // WHEN
    subject.publish(MatchType.MATCH, result);
  }

  @Test
  public void testExceptionsMatchMessagesCorrectly() throws Exception {
    // GIVEN
    PrintStreamPublisher<String> subject = new PrintStreamPublisher<>(ps);

    // WHEN
    subject.publish(MatchType.EXCEPTION_MATCH, result);
    String s = bos.toString();

    // THEN
    assertEquals(
        "candidate took 1 millis to execute\n" +
            "control took 1 millis to execute\n" +
            "example: candidate and control both threw exceptions, and the exceptions match\n", s);
  }

  @Test
  public void testMatchMessagesCorrectly() throws Exception {
    // GIVEN
    PrintStreamPublisher<String> subject = new PrintStreamPublisher<>(ps);

    // WHEN
    subject.publish(MatchType.MATCH, result);
    String s = bos.toString();

    // THEN
    assertEquals(
        "candidate took 1 millis to execute\n" +
            "control took 1 millis to execute\n" +
            "example: candidate and control both executed successfully and match\n", s);
  }

  @Test
  public void testControlExceptionMessagesCorrectly() throws Exception {
    // GIVEN
    PrintStreamPublisher<String> subject = new PrintStreamPublisher<>(ps);

    // WHEN
    subject.publish(MatchType.CONTROL_EXCEPTION, result);
    String s = bos.toString();

    // THEN
    assertEquals(
        "candidate took 1 millis to execute\n" +
            "control took 1 millis to execute\n" +
            "example: the candidate executed successfully but the control threw an exception\n", s);
  }

  @Test
  public void testCandidateExceptionMessagesCorrectly() throws Exception {
    // GIVEN
    PrintStreamPublisher<String> subject = new PrintStreamPublisher<>(ps);

    // WHEN
    subject.publish(MatchType.CANDIDATE_EXCEPTION, result);
    String s = bos.toString();

    // THEN
    assertEquals(
        "candidate took 1 millis to execute\n" +
            "control took 1 millis to execute\n" +
            "example: the control executed successfully but the candidate threw an exception\n", s);
  }

  @Test
  public void testExceptionMisMatchMessagesCorrectly() throws Exception {
    // GIVEN
    PrintStreamPublisher<String> subject = new PrintStreamPublisher<>(ps);

    // WHEN
    subject.publish(MatchType.EXCEPTION_MISMATCH, result);
    String s = bos.toString();

    // THEN
    assertEquals(
        "candidate took 1 millis to execute\n" +
            "control took 1 millis to execute\n" +
            "example: candidate and control both threw exceptions, but the exceptions don't match\n", s);
  }

  @Test
  public void testMisMatchMessagesCorrectly() throws Exception {
    // GIVEN
    PrintStreamPublisher<String> subject = new PrintStreamPublisher<>(ps);

    // WHEN
    subject.publish(MatchType.MISMATCH, result);
    String s = bos.toString();

    // THEN
    assertEquals(
        "candidate took 1 millis to execute\n" +
            "control took 1 millis to execute\n" +
            "example: candidate and control both executed successfully, but the responses don't match\n", s);
  }

}