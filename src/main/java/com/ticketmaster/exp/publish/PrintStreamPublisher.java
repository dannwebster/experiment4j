package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.Publisher;
import com.ticketmaster.exp.Result;

import java.io.PrintStream;
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
            PatternMatchCountNamer.DEFAULT);

    private final PrintStream printStream;
    private final DurationNamer<String> durationNamer;
    private final MatchCountNamer<String> matchCountNamer;
    private final Map<String, Integer> counts = new ConcurrentHashMap<>();

    public static <T> PrintStreamPublisher<T> from(PrintStream printStream, DurationNamer<String> durationNamer, MatchCountNamer<String> matchCountNamer) {
        return new PrintStreamPublisher(printStream, durationNamer, matchCountNamer);
    }

    private PrintStreamPublisher(PrintStream printStream, DurationNamer<String> durationNamer, MatchCountNamer<String> matchCountNamer) {
        this.printStream = printStream;
        this.durationNamer = durationNamer;
        this.matchCountNamer = matchCountNamer;
    }

    @Override
    public void publish(boolean wasMatch, Result<T> payload) {
        String candidateDn = durationNamer.name(payload.getName(), CANDIDATE);
        String controlDn = durationNamer.name(payload.getName(), CONTROL);
        String matchCountN = matchCountNamer.name(payload.getName(), wasMatch);

        int matchCount = counts.compute(matchCountN, (k, v) -> v == null ? 1 : v++ );

        printStream.println(matchCountN + "=" + matchCount);
        printStream.println(candidateDn + "=" + payload.getCandidateResult().getDuration().toMillis());
        printStream.println(controlDn + "=" + payload.getControlResult().getDuration().toMillis());

    }
}
