package com.ticketmaster.exp;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import com.ticketmaster.exp.util.Try;

public class OptimizedTrial<I, O> extends Trial<I, O> {

  public OptimizedTrial(
      String name,
      Function<I, O> control,
      Function<I, O> candidate,
      ExecutorService executorService,
      Function<Result<O>, Try<O>> returnChoice,
      BooleanSupplier doExperimentWhen,
      BiFunction<O, O, Boolean> sameWhen,
      BiFunction<Exception, Exception, Boolean> exceptionsSameWhen,
      Publisher<O> publisher,
      Clock clock)
  {
    super(
        name,
        control,
        candidate,
        executorService,
        returnChoice,
        doExperimentWhen,
        sameWhen,
        exceptionsSameWhen,
        publisher,
        clock);
  }
  
  @Override
  Result<O> runCandidateAndControl(I args) {
    TrialType returnType = determineReturnType();
    return asyncExperiment(args, returnType);
  }

  private TrialType determineReturnType() {
    Result<O> result = new Result<>(
        name,
        null,
        new TrialResult<O>(
            TrialType.CONTROL,
            Duration.ofMillis(0),
            new ReturnTypeException(TrialType.CONTROL),
            null),
        new TrialResult<O>(
            TrialType.CANDIDATE,
            Duration.ofMillis(0),
            new ReturnTypeException(TrialType.CANDIDATE),
            null));
    ReturnTypeException tryReturned =
        (ReturnTypeException) returnChoice.apply(result).exception().get();
    return tryReturned.getReturnType();
  }

  private Result<O> asyncExperiment(I args, TrialType returnType) {
    Callable<TrialResult<O>> callableControl = () -> observe(CONTROL, control, args);
    Callable<TrialResult<O>> callableCandidate = () -> observe(CANDIDATE, candidate, args);

    Instant timestamp = Instant.now();
    Future<TrialResult<O>> controlFuture = executorService.submit(callableControl);
    Future<TrialResult<O>> candidateFuture = executorService.submit(callableCandidate);
    
    Future<TrialResult<O>> returnFuture;
    if (returnType == TrialType.CONTROL) {
        returnFuture = controlFuture;
    } else {
        returnFuture = candidateFuture;
    }
    
    Result<O> result = null;
    try {
      result = new Result<>(
          name,
          timestamp,
          returnFuture.get(),
          returnFuture.get());
    } catch (InterruptedException|ExecutionException e) {
      throw new RuntimeException(e);
    }
    
    Callable<Result<O>> callableRunExperiment =
        () -> runExperimentAndPublishResults(controlFuture, candidateFuture, timestamp);
    executorService.submit(callableRunExperiment);
    
    return result;
  }
  
  private Result<O> runExperimentAndPublishResults(
          Future<TrialResult<O>> controlFuture,
          Future<TrialResult<O>> candidateFuture,
          Instant timestamp) {
    Result<O> result = null;
    try {
      result = new Result<>(
          name,
          timestamp,
          controlFuture.get(),
          candidateFuture.get());
    } catch (InterruptedException|ExecutionException e) {
      throw new RuntimeException(e);
    }
    
    MatchType matchType = determineMatch(result);
    publisher.publish(matchType, result);
    return result;
  }
  
  private static class ReturnTypeException extends Exception {
    private static final long serialVersionUID = -5745327595463184100L;
    
    private final TrialType returnType;
    
    public ReturnTypeException(TrialType returnType) {
      this.returnType = returnType;
    }
    
    public TrialType getReturnType() {
      return returnType;
    }

  }

}
