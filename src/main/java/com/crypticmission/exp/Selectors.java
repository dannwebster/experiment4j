package com.crypticmission.exp;

import java.time.Instant;
import java.util.Random;
import java.util.function.Supplier;

/**
 *
 * @author dannwebster
 */
public class Selectors {
    public static final Random INTS = new Random(Instant.now().toEpochMilli());

    private Selectors() {}

    public static Supplier<Boolean> percentage(int percent) {
        return () -> INTS.nextInt(100) < percent;
    }


}
