package com.crypticmission.exp;

import static com.crypticmission.exp.ExperimentBuilder.experiment;
import java.util.Comparator;
import java.util.Optional;
import org.junit.Test;

/**
 * Created by dannwebster on 10/12/14.
 */

public class ExperimentTest {
    @Test
    public void testSyntax() throws Exception {
        Comparator<Integer> c = (Integer a, Integer b) -> a - b;
        Experiment<String, Integer> e = experiment()
                .named("foo")
                .control(() -> "Foo")
                .candidate(() -> "Bar")
                .selector()
                .comparator(c)
                .cleaner((Optional<String> s) -> (s.isPresent()) ? s.get().length() : 0)
                .publisher(
                    publisher()
                        .measurer()
                        .durationNamePattern()
                        .durationNamePattern()
                 )
                .build();
                e.perform();
        ////////
        Experiment e = experiment("foo", (ExperimentBuilder b) -> b);
    }
}
