package com.ticketmaster.exp.util;

import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.TrialType;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Created by dannwebster on 4/19/15.
 */
public class ReturnChoices {
    ReturnChoices() {}

    public static <T> Function<Result<T>, Try<T>> ofType(TrialType type) {
        return (result) -> TrialType.CANDIDATE.equals(type) ?
            result.getCandidateResult().getTryResult():
            result.getControlResult().getTryResult();
    }


    public static <T> Function<Result<T>, Try<T>> candidateWhen(BooleanSupplier candidateWhen) {
        return (result) -> (candidateWhen.getAsBoolean()) ?
                result.getCandidateResult().getTryResult() :
                result.getControlResult().getTryResult();
    }

    public static <T> Function<Result<T>, Try<T>> alwaysControl() {
        return ofType(TrialType.CONTROL);
    }

    public static <T> Function<Result<T>, Try<T>> alwaysCandidate() {
        return ofType(TrialType.CANDIDATE);
    }

    public static <T> Function<Result<T>, Try<T>> findFastest() {
        return (result) -> result.getControlResult().getDuration().compareTo(result.getCandidateResult().getDuration()) <= 0 ?
                result.getControlResult().getTryResult() :
                result.getCandidateResult().getTryResult();

    }

    public static <T> Function<Result<T>, Try<T>> findBest() {
        Function<Result<T>, Try<T>> fastest = findFastest();
        return (result) -> result.getControlResult().getTryResult().isFailure() ?
                                   result.getCandidateResult().getTryResult() :
                               result.getCandidateResult().getTryResult().isFailure() ?
                                   result.getControlResult().getTryResult() :
                                   fastest.apply(result);
    }
}
