package com.ticketmaster.exp;

import com.ticketmaster.exp.util.Assert;
import com.ticketmaster.exp.util.Try;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by dannwebster on 10/12/14.
 */
public class Result<T> {

    private final String name;
    private final Instant timestamp;
    private final TrialResult<T> candidateResult;
    private final TrialResult<T> controlResult;

    public Result(String name, Instant timestamp, TrialResult<T> candidateResult, TrialResult<T> controlResult) {
        Assert.hasText(name, "name must be non-empty");
        Assert.notNull(timestamp, "timestamp must be non-null");
        Assert.notNull(controlResult, "controlResult must be non-null");
        Assert.notNull(candidateResult, "candidateResult must be non-null");
        this.name = name;
        this.timestamp = timestamp;
        this.candidateResult = candidateResult;
        this.controlResult = controlResult;
    }

    public String getName() { return name; }
    public Instant getTimestamp() { return timestamp; }

    public TrialResult<T> getCandidateResult() { return candidateResult; }
    public TrialResult<T> getControlResult() { return controlResult; }

    public <M> MatchType determineMatch(Function<T, M> simplifier,
                                        BiFunction<M, M, Boolean> sameWhen,
                                        BiFunction<? super Exception, ? super Exception, Boolean> exceptionsSameWhen) {
        List<M> values = Arrays
                .asList(this.getCandidateResult(), this.getControlResult())
                .stream()
                .map(TrialResult::getTryResult)
                .filter(Try::isSuccess)
                .map(Try::value)
                .map(Optional::get)
                .map( t -> simplifier.apply(t) )
                .collect(Collectors.toList());

        MatchType matchType;
        if (values.size() == 2) {
            if (sameWhen.apply(values.get(0), values.get(1))) {
                matchType = MatchType.MATCH;
            } else {
                matchType = MatchType.MISMATCH;
            }
        } else {
             List<Exception> exceptions = Arrays
                .asList(this.getCandidateResult(), this.getControlResult())
                .stream()
                .map(TrialResult::getTryResult)
                .filter(Try::isFailure)
                .map(Try::exception)
                .map(Optional::get)
                .collect(Collectors.toList());
            if (exceptions.size() == 2) {
                if (exceptionsSameWhen.apply(exceptions.get(0), exceptions.get(1))) {
                    matchType = MatchType.EXCEPTION_MATCH;
                } else {
                    matchType = MatchType.EXCEPTION_MISMATCH;
                }
            } else {
                matchType = (candidateResult.getTryResult().isFailure()) ?
                        MatchType.CANDIDATE_EXCEPTION : MatchType.CONTROL_EXCEPTION;
            }
        }
        return matchType;
    }
}
