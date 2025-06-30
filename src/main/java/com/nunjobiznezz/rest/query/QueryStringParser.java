package com.nunjobiznezz.rest.query;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.data.mongodb.core.query.Criteria;

public class QueryStringParser {

    public QueryStringParser() {
    }

    /**
     * Parse a filter string and generate MongoDB Criteria for the expression
     *
     * @param filterString Input string
     * @return Criteria
     */
    public Criteria parseCriteria(String filterString) {

        if (null == filterString) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        var preprocessedString = filterString.trim();
        if (preprocessedString.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        var tokens = performLexing(preprocessedString);

        var tree = performParsing(tokens);

        return performEmission(tree);
    }

    /**
     * Perform the emitter phase, using the parser output to create criteria
     * @param tree Criteria context from parsing.
     * @return Criteria for MongoDb
     */
    protected Criteria performEmission(RestQueryParser.QueryContext tree) {

        // Use the custom MongoCriteriaVisitor to visit the parse tree
        var emitter = new CriteriaVisitor();

        return emitter.visit(tree);
    }

    /**
     * Perform lexing of the input string
     * @param queryString Input text
     * @return Token stream to use in parsing
     */
    protected CommonTokenStream performLexing(String queryString) {

        CharStream charStream = CharStreams.fromString(queryString);

        var lexer = new RestQueryLexer(charStream);

        // Create a custom error listener
        CustomErrorListener errorListener = new CustomErrorListener();

        // Attach the listener to the lexer
        lexer.removeErrorListeners(); // Remove default console error listener
        lexer.addErrorListener(errorListener);

        var tokens = new CommonTokenStream(lexer);

        // Check for errors
        if (errorListener.hasErrors()) {
            throw new IllegalArgumentException(errorListener.getErrorMessages().toString());
        }

        return tokens;
    }

    /**
     * Perform the parsing phase using the output of the lexer
     * @param tokens Tokens from lexer
     * @return Criteria context used in emitter (visitor pattern)
     */
    protected RestQueryParser.QueryContext performParsing(CommonTokenStream tokens) {

        var parser = new RestQueryParser(tokens);

        var errorListener = new CustomErrorListener();

        // Attach the listener to the parser
        parser.removeErrorListeners(); // Remove default console error listener
        parser.addErrorListener(errorListener);

        var tree = parser.query();

        System.out.println("Parse Tree: " + tree.toStringTree(parser));

        // Check for errors
        if (errorListener.hasErrors()) {
            throw new IllegalArgumentException(errorListener.getErrorMessages().toString());
        }

        return tree;
    }
}
