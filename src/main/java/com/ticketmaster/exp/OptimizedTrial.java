package com.ticketmaster.exp;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import com.ticketmaster.exp.util.Assert;
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
    return null;
  }

}
