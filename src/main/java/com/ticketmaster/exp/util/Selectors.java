/*
 * Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ticketmaster.exp.util;

import java.time.Instant;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class Selectors {

  public static final int MAX_PERCENT = 100;
  public static final int MAX_PERMILLE = 1000;

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
      return Math.abs(supplier.getAsInt()) % denominator;
    }
  }

  private static class RandomInts implements IntSupplier {
    private final Random ints = new Random(Instant.now().toEpochMilli());

    @Override
    public int getAsInt() {
      return ints.nextInt();
    }
  }

  public static class LessThanThreshhold implements BooleanSupplier {
    private final IntSupplier valueSupplier;
    private final IntSupplier thresholdSupplier;

    @Override
    public boolean getAsBoolean() {
      int threshold = thresholdSupplier.getAsInt();
      if (threshold == 0) {
        return false;
      } else {
        int value = valueSupplier.getAsInt();
        return value <= threshold;
      }
    }

    public LessThanThreshhold(IntSupplier thresholdSupplier, IntSupplier valueSupplier) {
      this.thresholdSupplier = thresholdSupplier;
      this.valueSupplier = valueSupplier;
    }
  }

  Selectors() {
  }

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
    return new LessThanThreshhold(
        new ModInt(thresholdSupplier, denominator),
        new ModInt(new RandomInts(), denominator));
  }

  public static BooleanSupplier permille(int permille) {
    Assert.between(permille, 0, MAX_PERMILLE);
    return dynamicPermille(() -> permille);
  }

  public static BooleanSupplier percent(int percent) {
    Assert.between(percent, 0, MAX_PERCENT);
    return dynamicPercent(() -> percent);
  }

  public static BooleanSupplier dynamicPercentOfObjectHash(
      IntSupplier thresholdSupplier, Object object) {
    return new LessThanThreshhold(
        new ModInt(thresholdSupplier, MAX_PERCENT),
        new ModInt(() -> object.hashCode(), MAX_PERCENT));
  }

  public static BooleanSupplier dynamicPermilleOfObjectHash(
      IntSupplier thresholdSupplier, Object object) {
    return new LessThanThreshhold(
        new ModInt(thresholdSupplier, MAX_PERMILLE),
        new ModInt(() -> object.hashCode(), MAX_PERMILLE));
  }

  public static BooleanSupplier percentOfObjectHash(int percent, Object object) {
    Assert.between(percent, 0, MAX_PERCENT);
    return dynamicPercentOfObjectHash(() -> percent, object);
  }

  public static BooleanSupplier permilleOfObjectHash(int permille, Object object) {
    Assert.between(permille, 0, MAX_PERMILLE);
    return dynamicPermilleOfObjectHash(() -> permille, object);
  }
}
