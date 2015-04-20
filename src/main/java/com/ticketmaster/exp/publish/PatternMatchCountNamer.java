package com.ticketmaster.exp.publish;

import com.ticketmaster.exp.MatchType;

/**
 * Created by dannwebster on 4/18/15.
 */
public class PatternMatchCountNamer implements MatchCountNamer<String>{
    public static final String DEFAULT_PATTERN = "exp.%s.match.type.%s.count";
    public static final PatternMatchCountNamer DEFAULT = from(DEFAULT_PATTERN);
    private final String pattern;
    private final String trueString;
    private final String falseString;

    public static PatternMatchCountNamer from(String pattern, String trueString, String falseString) {
        return new PatternMatchCountNamer(pattern, trueString, falseString);
    }

    public static PatternMatchCountNamer from(String pattern) {
        return from(pattern, "true", "false");
    }

    private PatternMatchCountNamer(String pattern, String trueString, String falseString) {
        this.pattern = pattern;
        this.trueString = trueString;
        this.falseString = falseString;
    }

    @Override
    public String name(String experimentName, MatchType matchType) {
        return String.format(pattern, experimentName, matchType.name().toLowerCase());
    }
}
