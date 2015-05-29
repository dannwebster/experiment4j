package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.MatchType;
import com.ticketmaster.exp.Publisher;
import com.ticketmaster.exp.Result;

import java.io.PrintStream;

/**
 * Created by dannwebster on 5/28/15.
 */
public class PrintStreamPublisher<T> implements Publisher<T> {

    private PrintStream printStream;

    public PrintStreamPublisher() {
        this(System.out);
    }

    public PrintStreamPublisher(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void publish(MatchType matchType, Result<T> payload) {
        printStream.println("candidate took " + payload.getCandidateResult().getDuration().toMillis() + " millis to execute");
        printStream.println("control took " + payload.getControlResult().getDuration().toMillis() + " millis to execute");
        String message = "";
        switch (matchType) {
            case MATCH:
                message = "candidate and control both executed successfully and match";
                break;
            case EXCEPTION_MATCH:
                message = "candidate and control both threw exceptions, and the exceptions match";
                break;
            case MISMATCH:
                message = "candidate and control both executed successfully, but the responses don't match";
                break;
            case EXCEPTION_MISMATCH:
                message = "candidate and control both threw exceptions, but the exceptions don't match";
                break;
            case CONTROL_EXCEPTION:
                message = "the candidate executed successfully but the control threw an exception";
                break;
            case CANDIDATE_EXCEPTION:
                message = "the control executed successfully but the candidate threw an exception";
                break;
        }
        printStream.println(payload.getName() + ": " + message);
    }
}
