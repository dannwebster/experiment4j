package com.ticketmaster.exp;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.ticketmaster.exp.util.ReturnChoices;
import com.ticketmaster.exp.util.SameWhens;
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
    public static final String ARGS = "foo";

    Function<String, String> candidate = mock(Function.class);
    Function<String, String> control = mock(Function.class);
    Publisher<String> p = mock(Publisher.class);
    Clock c = mock(Clock.class);

    @Before
    public void setUp() throws Exception {
        when(c.instant()).thenAnswer((inv) -> Instant.EPOCH);
        when(candidate.apply(any())).thenReturn("candidate");
        when(control.apply(any())).thenReturn("control");
    }

    @Test
    public void testSimpleCallsPublish() throws Exception {

        // GIVEN
        Function<String, String> e = Experiment.<String, String>simple("my simple experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .publishedBy(p)
                .get();

        // WHEN
        String s = e.apply(ARGS);

        // THEN
        assertEquals("control", s);
        verify(p, times(1)).publish(Matchers.eq(MatchType.MISMATCH), Matchers.any(Result.class));
        verify(candidate, times(1)).apply(ARGS);
        verify(control, times(1)).apply(ARGS);
    }

    @Test
    public void testExperimentCallsPublishWithMatch() throws Exception {

        // GIVEN
        when(candidate.apply(any())).thenReturn("control");
        Experiment<String, String, String> e = Experiment.<String, String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .publishedBy(p)
                .get();

        // WHEN
        String s = e.apply(ARGS);

        // THEN
        assertEquals("control", s);
        verify(p, times(1)).publish(Matchers.eq(MatchType.MATCH), Matchers.any(Result.class));
        verify(candidate, times(1)).apply(ARGS);
        verify(control, times(1)).apply(ARGS);
    }

    @Test
    public void testReturnCandidate() throws Exception {

        // GIVEN
        Experiment<String, String, String> e = Experiment.<String, String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .returnChoice(ReturnChoices.alwaysCandidate())
                .publishedBy(p)
                .get();

        // WHEN
        String s = e.apply(ARGS);

        // THEN
        assertEquals("candidate", s);
        verify(p, times(1)).publish(Matchers.eq(MatchType.MISMATCH), Matchers.any(Result.class));
        verify(candidate, times(1)).apply(ARGS);
        verify(control, times(1)).apply(ARGS);
    }

    @Test
    public void testIgnoreExperiment() throws Exception {

        // GIVEN
        Experiment<String, String, String> e = Experiment.<String, String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .doExperimentWhen(Selectors.never())
                .publishedBy(p)
                .get();

        // WHEN
        String s = e.apply(ARGS);

        // THEN
        assertEquals("control", s);
        verify(control, times(1)).apply(ARGS);
        verify(candidate, never()).apply(ARGS);
        verify(p, never()).publish(any(), any());
    }

    @Rule
    public ExpectedException ex = ExpectedException.none();

    @Test
    public void testFailsOnControlFailure() throws Exception {

        // GIVEN
        when(control.apply(ARGS)).thenThrow(new IllegalArgumentException("control failed"));

        Experiment<String, String, String> e = Experiment.<String, String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .timedBy(c)
                .simplifiedBy(a -> a)
                .doExperimentWhen(Selectors.never())
                .publishedBy(p)
                .get();

        // EXPECT
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("control failed");

        // WHEN
        String s = e.apply(ARGS);
    }

    @Test
    public void testSucceedsOnCandiateFailure() throws Exception {

        // GIVEN
        when(candidate.apply(ARGS)).thenThrow(new IllegalArgumentException("control failed"));

        Experiment<String, String, String> e = Experiment.<String, String, String>named("my experiment")
                .control(control)
                .candidate(candidate)
                .simplifiedBy(a -> a)
                .doExperimentWhen(Selectors.never())
                .sameWhen(Objects::equals)
                .exceptionsSameWhen(SameWhens.classesMatch())
                .publishedBy(p)
                .get();

        // WHEN
        String s = e.apply(ARGS);

        // THEN
        assertEquals("control", s);
        verify(control, times(1)).apply(ARGS);
        verify(candidate, never()).apply(ARGS);
        verify(p, never()).publish(any(), any());
    }
}
