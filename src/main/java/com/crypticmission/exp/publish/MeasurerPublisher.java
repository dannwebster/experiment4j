package com.crypticmission.exp.publish;

import com.crypticmission.exp.Result;
import com.crypticmission.exp.Publisher;
import com.crypticmission.exp.util.Assert;
import java.util.function.Function;

/**
 * Created by dannwebster on 10/12/14.
 */
public class MeasurerPublisher<T> implements Publisher<T> {
    private final Measurer measurer;
    private final Function<Boolean, String> matchMetricNamer;
    private final DurationNamer durationNamer;

    public MeasurerPublisher(Measurer measurer, Function<Boolean, String> matchMetricNamer, DurationNamer durationNamer) {
        Assert.notNull(measurer, "measurer must be non-null");
        Assert.notNull(matchMetricNamer, "matchMetricNamer must be non-null");
        Assert.notNull(durationNamer, "durationNamer must be non-null");
        this.measurer = measurer;
        this.matchMetricNamer = matchMetricNamer;
        this.durationNamer = durationNamer;
    }

    @Override
    public void publish(boolean outputMatches, Result<T> payload) {
        String matchMetric = matchMetricNamer.apply(outputMatches);
        measurer.measureCount(matchMetric, 1);

        String controlDurationMetricName = matchMetricNamer.apply(outputMatches);
        measurer.measureDuration(controlDurationMetricName, payload.getControlPayload().getDuration());

        String candidateDurationMetricName = durationNamer.name(payload.getName(), );
        measurer.measureDuration(candidateDurationMetricName, payload.getCandidatePayload().getDuration());
    }
}
