package com.nunjobiznezz.rest.query;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This is the emitter that visits the parse tree and creates criteria
 */
public class CriteriaVisitor extends RestQueryBaseVisitor<Criteria> {

    @Override
    public Criteria visitQuery(RestQueryParser.QueryContext ctx) {

        var criteria = ctx.criteria().stream()
                .map(this::visit)
                .collect(Collectors.toList());

        if (criteria.isEmpty()) {
            throw new IllegalArgumentException("No criteria parsed");
        } else if (criteria.size() == 1) {
            return criteria.get(0);
        }

        return new Criteria().andOperator(criteria);
    }


    /**
     * Not sure why I need to do this, but I do
     * @param ctx the parse tree
     * @return expression
     */
    @Override
    public Criteria visitSimpleCriteria(RestQueryParser.SimpleCriteriaContext ctx) {
        return visit(ctx.expression());
    }

    /**
     * Not sure why I need to do this, but I do
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Criteria visitParenCriteria(RestQueryParser.ParenCriteriaContext ctx) {
        return visit(ctx.criteria());
    }

    /**
     * Process logical criteria
     *
     * @param ctx the parse tree
     * @return MongoDB Criteria 'or', 'and' or 'nor'
     */
    @Override
    public Criteria visitLogicalCriteria(RestQueryParser.LogicalCriteriaContext ctx) {

        var criteria = ctx.criteria();
        var operator = ctx.LOGICAL_OPERATOR().getText();

        var criteria1 = visit(criteria.get(0));
        var criteria2 = visit(criteria.get(1));

        return switch (Operator.fromValue(operator)) {
            case And -> new Criteria().andOperator(criteria1, criteria2);
            case Or -> new Criteria().orOperator(criteria1, criteria2);
            case Nor -> new Criteria().norOperator(criteria1, criteria2);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    /**
     * This is the point where you get n actual criteria based on a field and value
     *
     * @param ctx the parse tree
     * @return Criteria
     */
    @Override
    public Criteria visitSimpleExpression(RestQueryParser.SimpleExpressionContext ctx) {

        if (ctx.IDENTIFIER() == null || ctx.COMPARATOR() == null || ctx.value() == null) {
            var message = String.format("Expected expression at line %d, column %d", ctx.getStart().getLine(), ctx.start.getCharPositionInLine());
            throw new IllegalArgumentException(message);
        }

        var fieldName = ctx.IDENTIFIER().getText();
        var comparator = ComparatorEnum.fromValue(ctx.COMPARATOR().getText());
        var value = new ValueVisitor().visit(ctx.value());

        return createCriteria(fieldName, comparator, value);
    }

    /**
     * Process an 'IN' expression
     * @param ctx the parse tree
     * @return Criteria
     */
    @Override
    public Criteria visitSetExpression(RestQueryParser.SetExpressionContext ctx) {

//        if (ctx.IDENTIFIER() == null || ctx.array() == null || ctx.SET_COMPARATOR() == null) {
//            var message = String.format("Expected expression at line %d, column %d", ctx.getStart().getLine(), ctx.start.getCharPositionInLine());
//            throw new IllegalArgumentException(message);
//        }

        var fieldName = ctx.IDENTIFIER().getText();
        var comparator = ComparatorEnum.fromValue(ctx.SET_COMPARATOR().getText());
        var values = new ValueVisitor().visit(ctx.array());

        return createCriteria(fieldName, comparator, values);
    }

    @Override
    public Criteria visitRegexExpression(RestQueryParser.RegexExpressionContext ctx) {
        var fieldName = ctx.IDENTIFIER().getText();
        var regex = ctx.REGEX().getText();

        // Split the regex into pattern and options
        int lastSlash = regex.lastIndexOf('/');
        String pattern = regex.substring(1, lastSlash); // Extract pattern (remove leading and trailing '/')
        String options = (lastSlash + 1 < regex.length()) ? regex.substring(lastSlash + 1) : ""; // Extract options or empty string

        return Criteria.where(fieldName).regex(pattern, options);
    }


    /**
     * Create a criteria for an expression. This would be a good place to
     * override in a base class if you need to intercept logical field names
     * and change them, or combine them into a criteria, like expanding "informalName"
     * into "givenName" and "familyName". You could also enforce the type of value
     * needed for a field if you know the database structure.
     *
     * @param fieldName  Name of field
     * @param comparatorEnum Comparison type
     * @param value      Value object
     * @return Criteria object
     */
    protected Criteria createCriteria(String fieldName, ComparatorEnum comparatorEnum, Object value) {
        // MongoDB Criteria creation logic based on the operator
        return switch (comparatorEnum) {
            case Equals -> Criteria.where(fieldName).is(value);
            case NotEqual -> Criteria.where(fieldName).ne(value);
            case LessThan -> Criteria.where(fieldName).lt(value);
            case LessThanEqual -> Criteria.where(fieldName).lte(value);
            case GreaterThan -> Criteria.where(fieldName).gt(value);
            case GreaterThanEqual -> Criteria.where(fieldName).gte(value);
            case In -> {
                if (value instanceof Collection) {
                    yield Criteria.where(fieldName).in((Collection<?>) value);
                } else if (value.getClass().isArray()) {
                    yield Criteria.where(fieldName).in(Arrays.asList((Object[]) value));
                } else {
                    throw new IllegalArgumentException("Value must be a collection or array for IN");
                }
            }
            case Nin -> {
                if (value instanceof Collection) {
                    yield Criteria.where(fieldName).nin((Collection<?>) value);
                } else if (value.getClass().isArray()) {
                    yield Criteria.where(fieldName).nin(Arrays.asList((Object[]) value));
                } else {
                    throw new IllegalArgumentException("Value must be a collection or array for NIN");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported operator: " + comparatorEnum);
        };
    }

}
