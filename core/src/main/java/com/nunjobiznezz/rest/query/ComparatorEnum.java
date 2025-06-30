package com.nunjobiznezz.rest.query;

public enum ComparatorEnum {
    Equals("="),
    NotEqual("!="),
    LessThan("<"),
    GreaterThan(">"),
    LessThanEqual("<="),
    GreaterThanEqual(">="),
    Regex("~"),
    In("IN"),
    Nin("NIN");

    private final String value;

    ComparatorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ComparatorEnum fromValue(String value) {
        for (ComparatorEnum b : ComparatorEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
