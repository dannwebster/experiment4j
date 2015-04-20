package com.ticketmaster.exp.publish;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;

import com.ticketmaster.exp.MatchType;
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

    public static class Builder<T, K> {
        private Measurer measurer;
        private MatchCountNamer<K> matchCountNamer;
        private DurationNamer<K> durationNamer;

        private Builder() {}

        public Publisher<K> build() {
            return new MeasurerPublisher<K>(measurer, matchCountNamer, durationNamer);
        }

        public Builder<T, K> measurer(Measurer measurer) {
            this.measurer = measurer;
            return this;
        }

        public Builder<T, K> matchCountNamer(MatchCountNamer matchCountNamer) {
            this.matchCountNamer = matchCountNamer;
            return this;
        }

        public Builder<T, K> durationNamer(DurationNamer durationNamer) {
            this.durationNamer = durationNamer;
            return this;
        }

    }
    public static <T, K> Builder<T, K> builder() {
        return new Builder<>();
    }

    private MeasurerPublisher(Measurer<K> measurer, MatchCountNamer<K> matchCountNamer, DurationNamer<K> durationNamer) {
        Assert.notNull(measurer, "measurer must be non-null");
        Assert.notNull(matchCountNamer, "matchCountNamer must be non-null");
        Assert.notNull(durationNamer, "durationNamer must be non-null");
        this.measurer = measurer;
        this.matchCountNamer = matchCountNamer;
        this.durationNamer = durationNamer;
    }

    @Override
    public void publish(MatchType matchType, Result payload) {
        String name = payload.getName();
        measurer.measureCount(matchCountNamer.name(name, matchType), 1);
        measurer.measureDuration(durationNamer.name(name, CONTROL), payload.getControlResult().getDuration());
        measurer.measureDuration(durationNamer.name(name, CANDIDATE), payload.getCandidateResult().getDuration());
    }
}
