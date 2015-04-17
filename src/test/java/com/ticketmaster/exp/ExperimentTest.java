package com.ticketmaster.exp;

import java.util.Optional;
import org.junit.Test;

/**
 * Created by dannwebster on 10/12/14.
 */

public class ExperimentTest {

    @Test
    public void testBindSyntax() throws Exception {
        //String s = Science.<String, Integer>
        String s = Science.perform(
                Science.science("foo", (Science<String, Integer> f) -> {
                    f.selector(Selectors.percentage(30));
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
