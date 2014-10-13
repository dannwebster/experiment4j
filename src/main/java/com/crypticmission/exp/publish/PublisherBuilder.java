package com.crypticmission.exp.publish;

import com.crypticmission.exp.Experiment;
import com.crypticmission.exp.Publisher;
import java.util.function.Function;

/**
 *
 * @author dannwebster
 */
public class PublisherBuilder<T> {
    private Measurer measurer;
    private MatchCountNamer matchCountNamer;
    private DurationNamer durationNamer;

    private PublisherBuilder() {}

    public static <T> PublisherBuilder<T> publisher() {
        return new PublisherBuilder<T>();
    }

    public Publisher<T> build() {
        return new MeasurerPublisher(measurer, matchCountNamer, durationNamer);
    }

    public PublisherBuilder<T> measurer(Measurer measurer) {
        this.measurer = measurer;
        return this;
    }

    public PublisherBuilder<T> matchCountPattern(final String matchCountPattern) {
        this.matchCountNamer = new MatchCountNamer() {
            @Override
            public String name(String experimentName, boolean matches) {
                return String.format(matchCountPattern, experimentName, matches);
            }
        };
        return this;
    }

     public PublisherBuilder<T> durationPattern(final String durationPattern) {
        this.durationNamer = new DurationNamer() {
            @Override
            public String name(String experimentName, Experiment.TrialType trialType) {
                return String.format(durationPattern, experimentName, trialType);
            }
        };
        return this;
    }

    public PublisherBuilder<T> matchCountNamer(MatchCountNamer matchCountNamer) {
        this.matchCountNamer = matchCountNamer;
        return this;
    }

    public PublisherBuilder<T> durationNamer(DurationNamer durationNamer) {
        this.durationNamer = durationNamer;
        return this;
    }

}
