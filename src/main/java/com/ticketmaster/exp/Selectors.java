package com.ticketmaster.exp;

import java.time.Instant;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 *
 * @author dannwebster
 */
public class Selectors {
    private static final Random INTS = new Random(Instant.now().toEpochMilli());

    private Selectors() {}

    public static BooleanSupplier percentage(int percent) {
        return () -> INTS.nextInt(100) < percent;
    }
    public static final BooleanSupplier NEVER = () -> false;
    public static final BooleanSupplier ALWAYS = () -> true;


}
