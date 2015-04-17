package com.ticketmaster.exp;

import com.ticketmaster.exp.util.Assert;
import java.time.Instant;

/**
 * Created by dannwebster on 10/12/14.
 * {
 :experiment => "widget-permissions",
 :first      => :control,
 :timestamp  => <a-Time-instance>,

 :candidate => {
 :duration  => 2.5,
 :exception => nil,
 :value     => 42
 },

 :control => {
 :duration  => 25.0,
 :exception => nil,
 :value     => 24
 }
 }
 */
public class Result<T> {
    private final String name;
    private final Experiment.TrialType first;
    private final Instant timestamp;
    private final TrialResult<T> candidateResult;
    private final TrialResult<T> controlResult;

    public Result(String name, Experiment.TrialType first, Instant timestamp, TrialResult<T> candidateResult, TrialResult<T> controlResult) {
        Assert.hasText(name, "name must be non-empty");
        Assert.notNull(first, "first must be non-null");
        Assert.notNull(timestamp, "timestamp must be non-null");
        Assert.notNull(controlResult, "controlResult must be non-null");
        Assert.notNull(candidateResult, "candidateResult must be non-null");
        this.name = name;
        this.first = first;
        this.timestamp = timestamp;
        this.candidateResult = candidateResult;
        this.controlResult = controlResult;
    }

    public String getName() { return name; }
    public Experiment.TrialType getFirst() { return first; }
    public boolean getWasControlFirst() {return Experiment.TrialType.CONTROL.equals(first); }
    public boolean getWasCandidateFirst() {return Experiment.TrialType.CANDIDATE.equals(first); }
    public Instant getTimestamp() { return timestamp; }

    public TrialResult getCandidateResult() { return candidateResult; }
    public TrialResult getControlResult() { return controlResult; }

}
