package com.nunjobiznezz.rest.query;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class ValueVisitor extends RestQueryBaseVisitor<Object> {

    public ValueVisitor() {
    }

    @Override
    public Object visitQuotedStringValue(RestQueryParser.QuotedStringValueContext ctx) {
        return this.parseString(ctx.QUOTED_STRING().getText());
    }

    @Override
    public Object visitIntegerValue(RestQueryParser.IntegerValueContext ctx) {
        return this.parseInteger(ctx.INTEGER().getText());
    }

    @Override
    public Object visitFloatValue(RestQueryParser.FloatValueContext ctx) {
        return this.parseDouble(ctx.FLOAT().getText());
    }

    @Override
    public Object visitBooleanValue(RestQueryParser.BooleanValueContext ctx) {
        return this.parseBoolean(ctx.BOOLEAN().getText());
    }

    @Override
    public Object visitDateValue(RestQueryParser.DateValueContext ctx) {
        return this.parseLocalDateTime(ctx.ISO_DATE().getText());
    }

    @Override
    public Object visitObjectIdValue(RestQueryParser.ObjectIdValueContext ctx) {
        return this.parseObjectId(ctx.OBJECT_ID().getText());
    }

    @Override
    public Object visitArray(RestQueryParser.ArrayContext ctx) {
        return super.visitArray(ctx);
    }

    @Override
    public Object visitStringArray(RestQueryParser.StringArrayContext ctx) {
        return ctx.QUOTED_STRING()
                .stream()
                .map(this::visit)
                .toArray();
    }

    @Override
    public Object visitIntegerArray(RestQueryParser.IntegerArrayContext ctx) {
        return ctx.INTEGER()
                .stream()
                .map(this::visit)
                .toArray();
    }

    @Override
    public Object visitFloatArray(RestQueryParser.FloatArrayContext ctx) {
        return ctx.FLOAT()
                .stream()
                .map(this::visit)
                .toArray();
    }

    @Override
    public Object visitBooleanArray(RestQueryParser.BooleanArrayContext ctx) {
        return ctx.BOOLEAN()
                .stream()
                .map(this::visit)
                .toArray();
    }

    @Override
    public Object visitDateArray(RestQueryParser.DateArrayContext ctx) {
        return ctx.ISO_DATE()
                .stream()
                .map(this::visit)
                .toArray();
    }


    /**
     * Local Date/Time creator
     *
     * @param dateString input string
     * @return LocalDateTime
     */
    protected LocalDateTime parseLocalDateTime(String dateString) {
        var zonedDateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        return zonedDateTime.toLocalDateTime(); // Discard time zone info
    }

    /**
     * Integer creator
     *
     * @param integerString String value representing an integer
     * @return Integer
     */
    protected Integer parseInteger(String integerString) {
        return Integer.valueOf(integerString);
    }

    /**
     * Conversion of a string to a double value
     *
     * @param doubleString input string from parser
     * @return Double
     */
    protected Double parseDouble(String doubleString) {
        return Double.valueOf(doubleString);
    }

    /**
     * Well, I suppose there might have to be something done to a string
     * and here's where a person would do it.
     *
     * @param string Input from parsing
     * @return String value
     */
    protected String parseString(String string) {
        return string;
    }

    /**
     * Convert a string into a boolean
     * @param booleanString input string
     * @return Boolean
     */
    protected Boolean parseBoolean(String booleanString) {
        return Boolean.valueOf(booleanString);
    }

    /**
     * Create an object ID from a string. the input string must be 24 characters in
     * length and conform to mongoDb $oid rules
     * @param objectId String representing an object ID
     * @return Object ID
     */
    protected ObjectId parseObjectId(String objectId) {
        return new ObjectId(objectId);
    }

}
