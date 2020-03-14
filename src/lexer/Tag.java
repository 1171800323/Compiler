package lexer;

enum Tag {
    ID,
    INT, FLOAT, STRING, STRUCT, BOOL,
    TRUE, FALSE,
    NUM, REAL, WORDS,
    IF, ELSE, DO, WHILE, FOR, BREAK, RETURN,
    AND, OR, NOT,                    // "&&","||","!"
    EQ, NE, LE, GE, GT, LT,          // "==","!=","<=",">=",">","<"
    ADD, SUB, MUL, DIV, REMAIN,      // "+","-","*","/","%"
    INC, DEC,                        // "++","--"
    ASSIGN, SEMI, COMMA, DOT,        // "=", ";" , ",", "."
    LS, RS,                          // "(",")"
    LM, RM,                          // "[","]"
    LB, RB,                          // "{","}"
}