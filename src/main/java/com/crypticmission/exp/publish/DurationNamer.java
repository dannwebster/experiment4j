package com.crypticmission.exp.publish;

import com.crypticmission.exp.Experiment.TrialType;

/**
 * Created by dannwebster on 10/12/14.
 */
public interface DurationNamer {
    String name(String experimentName, TrialType trialType);
}
