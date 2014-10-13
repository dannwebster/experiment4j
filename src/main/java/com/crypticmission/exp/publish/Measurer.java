package com.crypticmission.exp.publish;

import java.time.Duration;

/**
 * Created by dannwebster on 10/12/14.
 */
public interface Measurer {
    public void measureDuration(String metricName, Duration duration);
    public void measureCount(String metricName, int count);
}
