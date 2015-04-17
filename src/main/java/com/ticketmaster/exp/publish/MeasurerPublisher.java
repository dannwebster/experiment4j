package com.ticketmaster.exp.publish;

import static com.ticketmaster.exp.Experiment.TrialType.CANDIDATE;
import static com.ticketmaster.exp.Experiment.TrialType.CONTROL;
import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.Publisher;
import com.ticketmaster.exp.util.Assert;

/**
 * Created by dannwebster on 10/12/14.
 */
public class MeasurerPublisher<K> implements Publisher {
    private final Measurer measurer;
    private final MatchCountNamer matchCountNamer;
    private final DurationNamer durationNamer;

    public MeasurerPublisher(Measurer<K> measurer, MatchCountNamer<K> matchCountNamer, DurationNamer<K> durationNamer) {
        Assert.notNull(measurer, "measurer must be non-null");
        Assert.notNull(matchCountNamer, "matchCountNamer must be non-null");
        Assert.notNull(durationNamer, "durationNamer must be non-null");
        this.measurer = measurer;
        this.matchCountNamer = matchCountNamer;
        this.durationNamer = durationNamer;
    }

    @Override
    public void publish(boolean outputMatches, Result payload) {
        String name = payload.getName();
        measurer.measureCount(matchCountNamer.name(name, outputMatches), 1);
        measurer.measureDuration(durationNamer.name(name, CONTROL), payload.getControlResult().getDuration());
        measurer.measureDuration(durationNamer.name(name, CANDIDATE), payload.getCandidateResult().getDuration());
    }
}
