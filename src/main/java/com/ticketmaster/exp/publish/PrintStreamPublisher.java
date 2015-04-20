package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.MatchType;
import com.ticketmaster.exp.Publisher;
import com.ticketmaster.exp.Result;
import com.ticketmaster.exp.util.Assert;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ticketmaster.exp.TrialType.CANDIDATE;
import static com.ticketmaster.exp.TrialType.CONTROL;

/**
 * Created by dannwebster on 4/18/15.
 */
public class PrintStreamPublisher<T> implements Publisher<T> {
    public static final PrintStreamPublisher DEFAULT = from(
            System.out,
            PatternDurationNamer.DEFAULT,
            PatternMatchCountNamer.DEFAULT,
            new ConcurrentMapMatchCounter<String>());

    private final PrintStream printStream;
    private final DurationNamer<String> durationNamer;
    private final MatchCountNamer<String> matchCountNamer;
    private final MatchCounter<String> matchCounter;

    public static <T> PrintStreamPublisher<T> from(PrintStream printStream, DurationNamer<String> durationNamer, MatchCountNamer<String> matchCountNamer, MatchCounter<T> matchCounter) {
        return new PrintStreamPublisher(printStream, durationNamer, matchCountNamer, matchCounter);
    }

    private PrintStreamPublisher(PrintStream printStream, DurationNamer<String> durationNamer, MatchCountNamer<String> matchCountNamer, MatchCounter<String> matchCounter) {
        Assert.notNull(printStream, "printStream must be non-null");
        Assert.notNull(durationNamer, "durationNamer must be non-null");
        Assert.notNull(matchCountNamer, "matchCountNamer must be non-null");
        Assert.notNull(matchCounter, "matchCounter must be non-null");

        this.printStream = printStream;
        this.durationNamer = durationNamer;
        this.matchCountNamer = matchCountNamer;
        this.matchCounter = matchCounter;
    }

    @Override
    public void publish(MatchType matchType, Result<T> payload) {
        String candidateDn = durationNamer.name(payload.getName(), CANDIDATE);
        String controlDn = durationNamer.name(payload.getName(), CONTROL);
        String matchCountN = matchCountNamer.name(payload.getName(), matchType);

        int matchCount = matchCounter.getAndIncrement(matchCountN);

        printStream.println(matchCountN + "=" + matchCount);
        printStream.println(candidateDn + "=" + payload.getCandidateResult().getDuration().toMillis());
        printStream.println(controlDn + "=" + payload.getControlResult().getDuration().toMillis());
    }


}
