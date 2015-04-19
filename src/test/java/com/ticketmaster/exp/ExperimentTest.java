package com.ticketmaster.exp;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import com.ticketmaster.exp.util.Selectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by dannwebster on 10/12/14.
 */

public class ExperimentTest {

    Callable<String> candidate = mock(Callable.class);
    Callable<String> control = mock(Callable.class);
    Publisher<String> p = mock(Publisher.class);
    Clock c = mock(Clock.class);

    @Before
    public void setUp() throws Exception {
        when(c.instant()).thenAnswer((inv) -> Instant.EPOCH);
        when(candidate.call()).thenReturn("candidate");
        when(control.call()).thenReturn("control");
    }

    @Test
    public void testSimpleCallsPublish() throws Exception {

        // GIVEN
        Callable<String> e = Experiment.<String>simple("my simple experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .publishedBy(p)
                .build();

        // WHEN
        String s = e.call();

        // THEN
        assertEquals("control", s);
        verify(p, times(1)).publish(Matchers.eq(false), Matchers.any(Result.class));
        verify(candidate, times(1)).call();
        verify(control, times(1)).call();
    }

    @Test
    public void testExperimentCallsPublishWithMatch() throws Exception {

        // GIVEN
        when(candidate.call()).thenReturn("control");
        Experiment<String, String> e = Experiment.<String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .publishedBy(p)
                .build();

        // WHEN
        String s = e.call();

        // THEN
        assertEquals("control", s);
        verify(p, times(1)).publish(Matchers.eq(true), Matchers.any(Result.class));
        verify(candidate, times(1)).call();
        verify(control, times(1)).call();
    }

    @Test
    public void testReturnCandidate() throws Exception {

        // GIVEN
        Experiment<String, String> e = Experiment.<String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .returnCandidateWhen(Selectors.ALWAYS)
                .publishedBy(p)
                .build();

        // WHEN
        String s = e.call();

        // THEN
        assertEquals("candidate", s);
        verify(p, times(1)).publish(Matchers.eq(false), Matchers.any(Result.class));
        verify(candidate, times(1)).call();
        verify(control, times(1)).call();
    }

    @Test
    public void testIgnoreExperiment() throws Exception {

        // GIVEN
        Experiment<String, String> e = Experiment.<String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .doExperimentWhen(Selectors.NEVER)
                .publishedBy(p)
                .build();

        // WHEN
        String s = e.call();

        // THEN
        assertEquals("control", s);
        verify(control, times(1)).call();
        verify(candidate, never()).call();
        verify(p, never()).publish(anyBoolean(), any());
    }

    @Rule
    public ExpectedException ex = ExpectedException.none();

    @Test
    public void testFailsOnControlFailure() throws Exception {

        // GIVEN
        when(control.call()).thenThrow(new IllegalArgumentException("control failed"));

        Experiment<String, String> e = Experiment.<String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .doExperimentWhen(Selectors.NEVER)
                .publishedBy(p)
                .build();

        // EXPECT
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("control failed");

        // WHEN
        String s = e.call();
    }

    @Test
    public void testSucceedsOnCandiateFailure() throws Exception {

        // GIVEN
        when(candidate.call()).thenThrow(new IllegalArgumentException("control failed"));

        Experiment<String, String> e = Experiment.<String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .simplifiedBy(a -> a)
                .doExperimentWhen(Selectors.NEVER)
                .sameWhen(Objects::equals)
                .publishedBy(p)
                .build();

        // WHEN
        String s = e.call();

        // THEN
        assertEquals("control", s);
        verify(control, times(1)).call();
        verify(candidate, never()).call();
        verify(p, never()).publish(anyBoolean(), any());
    }
}
