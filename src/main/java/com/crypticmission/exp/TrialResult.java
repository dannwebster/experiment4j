package com.crypticmission.exp;

import com.crypticmission.exp.Experiment.TrialType;
import com.crypticmission.exp.util.Assert;
import com.crypticmission.exp.util.Tryz;
import java.time.Duration;
import java.util.Optional;
import javaslang.either.Either;
import javaslang.exception.Try;

/**
 * Created by dannwebster on 10/12/14.
 */
public class TrialResult<T> {
    private final TrialType trialType;
    private final Duration duration;
    private final Try<T> result;

    public TrialResult(TrialType trialType, Duration duration, Exception exception, T value) {
        Assert.notNull(trialType, "trialType must be non-null");
        Assert.notNull(duration, "duration must be non-null");
        this.trialType = trialType;
        this.duration = duration;
        this.result = Tryz.of(value, exception);
    }

    public TrialType getTrialType() { return trialType; }
    public Duration getDuration() { return duration; }
    public Try<T> getTryResult() { return result; }
}
