grammar RestQuery;

// Entry point
query
    : criteria+ EOF
    ;

// Criteria
criteria
    : criteria LOGICAL_OPERATOR criteria    # LogicalCriteria
    | '(' criteria ')'                      # ParenCriteria
    | expression                            # SimpleCriteria
    ;

// Expressions
expression
    : IDENTIFIER COMPARATOR value               # SimpleExpression
    | IDENTIFIER SET_COMPARATOR '[' array ']'   # SetExpression
    | IDENTIFIER REGEX_COMPARATOR REGEX         # RegexExpression
   ;

// Arrays with type enforcement
array
    : stringArray
    | integerArray
    | floatArray
    | booleanArray
    | dateArray
    ;

// Type-specific arrays
stringArray
    : QUOTED_STRING (',' QUOTED_STRING)*
    ;

integerArray
    : INTEGER (',' INTEGER)*
    ;

floatArray
    : FLOAT (',' FLOAT)*
    ;

booleanArray
    : BOOLEAN (',' BOOLEAN)*
    ;

dateArray
    : ISO_DATE (',' ISO_DATE)*
    ;

// Values
value
    : QUOTED_STRING         # QuotedStringValue
    | INTEGER               # IntegerValue
    | FLOAT                 # FloatValue
    | BOOLEAN               # BooleanValue
    | ISO_DATE              # DateValue
    | OBJECT_ID             # ObjectIdValue
    ;

// Tokens
LOGICAL_OPERATOR: 'AND' | 'OR' | 'NOR';
COMPARATOR: '>=' | '<=' | '>' | '<' | '=' | '!=';
SET_COMPARATOR:  'IN' | 'NIN';
REGEX_COMPARATOR: '~';

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
QUOTED_STRING: '"' (ESC | ~["\\])* '"';
INTEGER: '-'? DIGIT+;
FLOAT: '-'? DIGIT+ '.' DIGIT+;
BOOLEAN: 'true' | 'false';
ISO_DATE: DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT 'T' DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT
          ('.' DIGIT DIGIT DIGIT)?
          ('Z' | ('+' | '-') DIGIT DIGIT ':' DIGIT DIGIT);
OBJECT_ID: HEX HEX HEX HEX HEX HEX HEX HEX
           HEX HEX HEX HEX HEX HEX HEX HEX
           HEX HEX HEX HEX HEX HEX HEX HEX;

// Regular expression literal
// Regular expression literal
REGEX: '/' (ESC | ~[/\\])* '/' REGEX_FLAGS?;

// Allowed regex flags
REGEX_FLAGS: [imxsu]+;


// Lexer rules
fragment DIGIT: [0-9];
fragment HEX: [0-9a-fA-F];
fragment ESC: '\\' ["\\/bfnrt];

// Ignored characters
WS: [ \t\r\n]+ -> skip;
