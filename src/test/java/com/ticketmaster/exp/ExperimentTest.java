package com.ticketmaster.exp;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import org.junit.Test;
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

    @Test
    public void testSimple() throws Exception {
        PrimitiveIterator.OfInt is = IntStream.iterate(0, (i) -> i+1 ).iterator();
        Publisher<String> p = mock(Publisher.class);
        Clock c = mock(Clock.class);
        when(c.instant()).thenAnswer((inv) -> Instant.ofEpochMilli(is.nextInt()));

        Callable<String> e = Experiment.<String>simple("my simple experiment")
                .candidate(() -> "foo")
                .control(() -> "bar")
                .timedBy(c)
                .sameWhen((a, b) -> a.compareTo(b))
                .publishedBy(p)
                .build();

        String s = e.call();
    }

    @Test
    public void testBindSyntax() throws Exception {
        PrimitiveIterator.OfInt is = IntStream.iterate(0, (i) -> i+1 ).iterator();
        Publisher<String> p = mock(Publisher.class);
        Clock c = mock(Clock.class);
        when(c.instant()).thenAnswer((inv) -> Instant.ofEpochMilli(is.nextInt()));

        Experiment<String, String> e = Experiment.<String, String>named("my experiment")
                .candidate(() -> "foo")
                .control(() -> "foo")
                .timedBy(c)
                .simplifiedBy(a -> a)
                .sameWhen((a, b) -> a.compareTo(b))
                .publishedBy(p)
                .build();

        String s = e.call();
        verify(p, times(1)).publish(Matchers.eq(true), Matchers.any(Result.class));
    }
}
