package com.nunjobiznezz.rest.query;

public enum Operator {
    And("AND"),
    Or("OR"),
    Nor("NOR");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static Operator fromValue(String value) {
        for (Operator b : Operator.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
