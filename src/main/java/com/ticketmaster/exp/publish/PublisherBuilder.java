package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.Experiment;
import com.ticketmaster.exp.Publisher;

/**
 *
 * @author dannwebster
 */
public class PublisherBuilder<T, K> {
    private Measurer measurer;
    private MatchCountNamer<K> matchCountNamer;
    private DurationNamer<K> durationNamer;

    private PublisherBuilder() {}

    public static <T, K> PublisherBuilder<T, K> publisher() {
        return new PublisherBuilder<T, K>();
    }

    public Publisher<K> build() {
        return new MeasurerPublisher<K>(measurer, matchCountNamer, durationNamer);
    }

    public PublisherBuilder<T, K> measurer(Measurer measurer) {
        this.measurer = measurer;
        return this;
    }

    public PublisherBuilder<T, K> matchCountPattern(final String matchCountPattern) {
        this.matchCountNamer = new MatchCountNamer() {
            @Override
            public String name(String experimentName, boolean matches) {
                return String.format(matchCountPattern, experimentName, matches);
            }
        };
        return this;
    }

     public PublisherBuilder<T, K> durationPattern(final String durationPattern) {
        this.durationNamer = new DurationNamer() {
            @Override
            public String name(String experimentName, Experiment.TrialType trialType) {
                return String.format(durationPattern, experimentName, trialType);
            }
        };
        return this;
    }

    public PublisherBuilder<T, K> matchCountNamer(MatchCountNamer matchCountNamer) {
        this.matchCountNamer = matchCountNamer;
        return this;
    }

    public PublisherBuilder<T, K> durationNamer(DurationNamer durationNamer) {
        this.durationNamer = durationNamer;
        return this;
    }

}
