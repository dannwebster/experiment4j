package com.crypticmission.exp;

import static com.crypticmission.exp.Science.perform;
import java.util.Optional;
import org.junit.Test;
import static com.crypticmission.exp.Science.science;
import static com.crypticmission.exp.Selectors.percentage;

/**
 * Created by dannwebster on 10/12/14.
 */

public class ExperimentTest {

    @Test
    public void testBindSyntax() throws Exception {
        //String s = Science.<String, Integer>
        String s = perform(
            science("foo", (Science<String, Integer> f) -> {
                f.selector(percentage(30));
                f.cleaner((Optional<String> o) -> o.isPresent() ? o.get().length() : 0);
                f.comparator((Integer a, Integer b) -> a - b);
            })
            .expermient((Science<String, Integer> f) -> {
                f.candidate(() -> "foo");
                f.control(() -> "bar");
            })
        );
    }
}
