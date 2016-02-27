/**
 * 
 */
package com.ticketmaster.exp;

import java.util.function.Function;

import com.ticketmaster.exp.util.Try;

/**
 * @author akaiser
 *
 */
public abstract class AsyncReturnChoice<O> implements Function<Result<O>, Try<O>> {

    @Override
    public Try<O> apply(Result<O> t) {
        // Probably should just have this throw an exception
        // because it won't / shouldn't be used
        return null;
    }
    
    public abstract TrialType getReturnChoice();

}
