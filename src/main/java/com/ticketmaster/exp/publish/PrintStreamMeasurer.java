package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.util.Assert;

import java.io.PrintStream;
import java.time.Duration;

/**
 * Created by dannwebster on 4/18/15.
 */
public class PrintStreamMeasurer implements Measurer<String> {
    public static final PrintStreamMeasurer DEFAULT = from(
            System.out,
            new ConcurrentMapMatchCounter<String>());

    private final PrintStream printStream;
    private final MatchCounter<String> matchCounter;

    public static PrintStreamMeasurer from(PrintStream printStream, MatchCounter<String> matchCounter) {
        return new PrintStreamMeasurer(printStream, matchCounter);
    }

    private PrintStreamMeasurer(PrintStream printStream, MatchCounter<String> matchCounter) {
        Assert.notNull(printStream, "printStream must be non-null");
        Assert.notNull(matchCounter, "matchCounter must be non-null");

        this.printStream = printStream;
        this.matchCounter = matchCounter;
    }

    @Override
    public void measureDuration(String metricKey, Duration duration) {
        printStream.println(metricKey + "=" + duration.toMillis());
    }

    @Override
    public void measureCount(String metricKey, int count) {
        int matchCount = matchCounter.getAndIncrement(metricKey);
        printStream.println(metricKey + "=" + matchCount);
    }
}
