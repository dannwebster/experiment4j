package com.crypticmission.exp.publish;

/**
 * Created by dannwebster on 10/12/14.
 */
public interface DurationNamer {
    String name(String experimentName, boolean wasControl);
}
