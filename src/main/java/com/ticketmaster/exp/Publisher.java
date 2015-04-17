package com.ticketmaster.exp;

/**
 * Created by dannwebster on 10/12/14.
 */
public interface Publisher<T> {
    void publish(boolean wasMatch, Result<T> payload);
}
