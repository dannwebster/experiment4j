package com.ticketmaster.exp.util;

import com.ticketmaster.exp.Result;

import java.time.Instant;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 *
 * @author dannwebster
 */
public class Selectors {

    public static class Permille implements BooleanSupplier {
        private final Random ints = new Random(Instant.now().toEpochMilli());
        private final int permille;

        @Override
        public boolean getAsBoolean() {
            return ints.nextInt(1000) < permille;
        }

        public Permille(int permille) {
            Assert.between(permille, 0, 1000+1);
            this.permille = permille;
        }
    }

    public static class Percent implements BooleanSupplier {
        private final Random ints = new Random(Instant.now().toEpochMilli());
        private final int percent;

        @Override
        public boolean getAsBoolean() {
            return ints.nextInt(100) < percent;
        }

        public Percent(int percent) {
            Assert.between(percent, 0, 100+1);
            this.percent = percent;
        }
    }

    Selectors() {}

    public static BooleanSupplier permille(int permille) {
        return new Permille(permille);
    }
    public static BooleanSupplier percent(int percent) {
        return new Percent(percent);
    }
    public static final BooleanSupplier NEVER = () -> false;
    public static final BooleanSupplier ALWAYS = () -> true;



}
