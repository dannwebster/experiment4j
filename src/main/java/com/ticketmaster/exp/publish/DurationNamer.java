package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.Experiment;

/**
 * Created by dannwebster on 10/12/14.
 */
public interface DurationNamer<K> {
    K name(String experimentName, Experiment.TrialType trialType);
}
