package com.nunjobiznezz.rest.query;

public enum TypeSpecifierEnum {

    Boolean("boolean"),
    Date("date"),
    Float("float"),
    Integer("int"),
    Number("number"),
    Object("object"),
    ObjectId("oid"),
    String("string");

    private final String value;

    TypeSpecifierEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TypeSpecifierEnum fromValue(String value) {
        for (TypeSpecifierEnum b : TypeSpecifierEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
