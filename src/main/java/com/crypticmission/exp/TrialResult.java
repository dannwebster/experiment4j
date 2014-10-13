package com.crypticmission.exp;

import com.crypticmission.exp.Experiment.TrialType;
import com.crypticmission.exp.util.Assert;
import java.time.Duration;
import java.util.Optional;

/**
 * Created by dannwebster on 10/12/14.
 */
public class TrialResult<T> {
    private final TrialType trialType;
    private final Duration duration;
    private final Optional<Exception> exception;
    private final Optional<T> value;

    public TrialResult(TrialType trialType, Duration duration, Exception exception, T value) {
        Assert.notNull(trialType, "trialType must be non-null");
        Assert.notNull(duration, "duration must be non-null");
        this.trialType = trialType;
        this.duration = duration;
        this.exception = Optional.ofNullable(exception); 
        this.value = Optional.ofNullable(value); 
    }

    public TrialType getTrialType() { return trialType; }
    public Duration getDuration() { return duration; }
    public Optional<Exception> getException() { return exception; }
    public Optional<T> getValue() { return value; }
}
