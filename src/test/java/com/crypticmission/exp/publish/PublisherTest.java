package com.crypticmission.exp.publish;

import com.crypticmission.exp.Publisher;
import static com.crypticmission.exp.publish.PublisherBuilder.publisher;
import java.time.Duration;
import org.junit.Test;

/**
 *
 * @author dannwebster
 */
public class PublisherTest {
    @Test
    public void testPublisherSyntax() throws Exception {
        Measurer measurer = new Measurer() {

            @Override
            public void measureDuration(String metricName, Duration duration) {
            }

            @Override
            public void measureCount(String metricName, int count) {
            }
        };
        Publisher publisher = publisher()
                .durationPattern(null)
                .matchCountPattern(null)
                .measurer(measurer)
                .build();
    }

}
