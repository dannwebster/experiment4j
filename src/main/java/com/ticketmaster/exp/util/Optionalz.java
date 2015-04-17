package com.ticketmaster.exp.util;

import java.util.Optional;
import javaslang.option.Option;

/**
 *
 * @author dannwebster
 */
public class Optionalz {
    public static <T> Optional<T> optional(Option<T> option) {
        return Optional.ofNullable(option.orElse(null));
    }

}
