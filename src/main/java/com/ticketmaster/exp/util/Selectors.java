package com.ticketmaster.exp.util;

import java.time.Instant;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 *
 * @author dannwebster
 */
public class Selectors {
    public static class Percentage implements BooleanSupplier {
        private final Random INTS = new Random(Instant.now().toEpochMilli());
        private final int percentage;

        @Override
        public boolean getAsBoolean() {
            return INTS.nextInt(100) < percentage;
        }

        public Percentage(int percentage) {
            this.percentage = percentage;
        }
    }

    Selectors() {}

    public static BooleanSupplier percentage(int percent) {
        return new Percentage(percent);
    }
    public static final BooleanSupplier NEVER = () -> false;
    public static final BooleanSupplier ALWAYS = () -> true;


}
