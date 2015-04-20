package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.TrialType;

/**
 * Created by dannwebster on 4/18/15.
 */
public class PatternDurationNamer implements DurationNamer<String> {
    public static final String DEFAULT_PATTERN = "exp.%s.trial.type.%s.dur";
    public static final PatternDurationNamer DEFAULT = from(DEFAULT_PATTERN);

    private final String pattern;

    public static PatternDurationNamer from(String pattern) {
        return new PatternDurationNamer(pattern);
    }
    private PatternDurationNamer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String name(String experimentName, TrialType trialType) {
        return String.format(pattern, experimentName, trialType.name().toLowerCase());
    }
}
