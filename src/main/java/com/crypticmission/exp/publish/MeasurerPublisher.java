package com.crypticmission.exp.publish;

import com.crypticmission.exp.Experiment;
import static com.crypticmission.exp.Experiment.TrialType.CANDIDATE;
import static com.crypticmission.exp.Experiment.TrialType.CONTROL;
import com.crypticmission.exp.Result;
import com.crypticmission.exp.Publisher;
import com.crypticmission.exp.util.Assert;
import java.util.function.Function;

/**
 * Created by dannwebster on 10/12/14.
 */
public class MeasurerPublisher implements Publisher {
    private final Measurer measurer;
    private final MatchCountNamer matchCountNamer;
    private final DurationNamer durationNamer;

    public MeasurerPublisher(Measurer measurer, MatchCountNamer matchCountNamer, DurationNamer durationNamer) {
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
