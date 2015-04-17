package com.ticketmaster.exp.publish;

import java.time.Duration;

/**
 * Created by dannwebster on 10/12/14.
 */
public interface Measurer<K> {
    public void measureDuration(K metricKey, Duration duration);
    public void measureCount(K metricKey, int count);
}
