package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.Publisher;

import java.time.Duration;

import org.junit.Test;

/**
 *
 * @author dannwebster
 */
public class PublisherTest {
    @Test
    public void testPublisherSyntax() throws Exception {
        Measurer measurer = new Measurer<String>() {

            @Override
            public void measureDuration(String metricName, Duration duration) {
            }

            @Override
            public void measureCount(String metricName, int count) {
            }
        };
        Publisher publisher = PublisherBuilder.publisher()
                .durationPattern(null)
                .matchCountPattern(null)
                .measurer(measurer)
                .build();
    }

}
