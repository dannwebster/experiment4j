package com.ticketmaster.exp.util;

import java.time.Instant;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 *
 * @author dannwebster
 */
public class Selectors {


    public static class ModInt implements IntSupplier {
        private final IntSupplier supplier;
        private final int denominator;

        public ModInt(IntSupplier supplier, int denominator) {
            Assert.between(denominator, 0, Integer.MAX_VALUE);
            this.supplier = supplier;
            this.denominator = denominator;
        }

        @Override
        public int getAsInt() {
            return supplier.getAsInt() % denominator;
        }
    }

    private static class RandomInts implements IntSupplier {
        private final Random ints = new Random(Instant.now().toEpochMilli());

        @Override
        public int getAsInt() {
            return Math.abs(ints.nextInt());
        }
    }

    public static class Per implements BooleanSupplier {
        private final IntSupplier valueSupplier;
        private final IntSupplier thresholdSupplier;

        @Override
        public boolean getAsBoolean() {
            int value = valueSupplier.getAsInt();
            int threshold = thresholdSupplier.getAsInt();
            return value < threshold;
        }

        public Per(IntSupplier thresholdSupplier, IntSupplier valueSupplier) {
            this.thresholdSupplier = thresholdSupplier;
            this.valueSupplier = valueSupplier;
        }
    }

    Selectors() {}

    private static final BooleanSupplier NEVER = () -> false;

    private static final BooleanSupplier ALWAYS = () -> true;

    public static BooleanSupplier never() {
        return NEVER;
    }

    public static BooleanSupplier always() {
        return ALWAYS;
    }

    public static BooleanSupplier dynamicPercent(IntSupplier thresholdSupplier) {
        return dynamicPerDenom(100, thresholdSupplier);
    }

    public static BooleanSupplier dynamicPermille(IntSupplier thresholdSupplier) {
        return dynamicPerDenom(1000, thresholdSupplier);
    }

    public static BooleanSupplier dynamicPerDenom(int denominator, IntSupplier thresholdSupplier) {
        return new Per(new ModInt(thresholdSupplier, denominator), new ModInt(new RandomInts(), denominator));
    }

    public static BooleanSupplier permille(int permille) {
        Assert.between(permille, 0, 1000+1);
        int finalPermille = permille == 1000 ? 999 : permille;
        return dynamicPermille(() -> finalPermille);
    }

    public static BooleanSupplier percent(int percent) {
        Assert.between(percent, 0, 100+1);
        int finalPercent = percent == 100 ? 99 : percent;
        return dynamicPercent(() -> finalPercent);
    }

    public static BooleanSupplier dynamicPercentOfObjectHash(IntSupplier thresholdSupplier, Supplier objectSupplier) {
        return new Per(new ModInt(thresholdSupplier, 100), new ModInt(() -> objectSupplier.get().hashCode(), 100));
    }
    public static BooleanSupplier dynamicPermilleOfObjectHash(IntSupplier thresholdSupplier, Supplier objectSupplier) {
        return new Per(new ModInt(thresholdSupplier, 1000), new ModInt(() -> objectSupplier.get().hashCode(), 1000));
    }
    public static BooleanSupplier percentOfObjectHash(int percent, Supplier objectSupplier) {
        return new Per(() -> percent, new ModInt(() -> objectSupplier.get().hashCode(), 100));
    }
    public static BooleanSupplier permilleOfObjectHash(int permille, Supplier objectSupplier) {
        return new Per(() -> permille, new ModInt(() -> objectSupplier.get().hashCode(), 1000));
    }
}
