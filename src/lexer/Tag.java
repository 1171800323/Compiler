package lexer;

public enum Tag {
    ID("id");
//    INT("int"), FLOAT("float"), CHAR("char"), STRUCT, BOOL,
//    TRUE, FALSE,
//    NUM, REAL, WORDS,
//    IF, ELSE, DO, WHILE, FOR, BREAK, CONTINUE,
//    PROC, CALL, RETURN,
//    AND, OR, NOT,                    // "&&","||","!"
//    EQ, NE, LE, GE, GT, LT,          // "==","!=","<=",">=",">","<"
//    ADD, SUB, MUL, DIV, REMAIN,      // "+","-","*","/","%"
//    INC, DEC,                        // "++","--"
//    ASSIGN, SEMI, COMMA, DOT,        // "=", ";" , ",", "."
//    LS, RS,                          // "(",")"
//    LM, RM,                          // "[","]"
//    LB, RB;                          // "{","}"
    private String value;

    Tag(String tag){
        this.value = tag;
    }
    public String getValue(){
        return this.getValue();
    }
    public static Tag fromString(String tag){
        for (Tag t : Tag.values()) {
            if (t.value.equals(tag))
                return t;
        }
        return null;
    }
}