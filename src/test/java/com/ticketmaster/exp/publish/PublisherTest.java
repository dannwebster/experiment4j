package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.Publisher;

import java.time.Duration;
import java.time.Instant;

import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.TrialResult;
import org.junit.Test;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;
import static org.mockito.Mockito.*;

/**
 *
 * @author dannwebster
 */
public class PublisherTest {
    @Test
    public void testPublisherSyntax() throws Exception {
        // GIVEN
        Measurer<String> m = mock(Measurer.class);

        DurationNamer<String> dn = PatternDurationNamer.DEFAULT;
        MatchCountNamer<String> mc = PatternMatchCountNamer.DEFAULT;


        Duration candidateD = Duration.ofSeconds(5);
        Duration controlD = Duration.ofSeconds(10);
        Result<String> result = new Result(
                "experiment",
                Instant.now(),
                new TrialResult(CANDIDATE, candidateD, null, "foo"),
                new TrialResult(CONTROL, controlD, null, "bar")
        );

        // WHEN
        Publisher publisher = MeasurerPublisher.builder()
                .matchCountNamer(mc)
                .durationNamer(dn)
                .measurer(m)
                .build();

        publisher.publish(true, result);

        // THEN
        verify(m, times(1)).measureCount("exp.experiment.match.true.count", 1);
        verify(m, times(1)).measureDuration("exp.experiment.trial.type.CANDIDATE.dur", candidateD);
        verify(m, times(1)).measureDuration("exp.experiment.trial.type.CONTROL.dur", controlD);
    }

}
