package lexer;

public enum Tag {
    ID("id"), NOTE("note"),
    INT("int"), FLOAT("float"), CHAR("char"), STRUCT("struct"), BOOL("bool"),
    TRUE("true"), FALSE("false"),
    NUM("num"), REAL("real"), WORDS("words"), CHARACTER("character"),
    OCT("oct"), HEX("hex"),
    IF("if"), ELSE("else"), DO("do"), WHILE("while"), FOR("for"), BREAK("break"), CONTINUE("continue"),
    PROC("proc"), CALL("call"), RETURN("return"),
    AND("&&"), OR("||"), NOT("!"),
    EQ("=="), NE("!="), LE("<="), GE(">="), GT(">"), LT("<"),
    ADD("+"), SUB("-"), MUL("*"), DIV("/"), REMAIN("%"),
    INC("++"), DEC("--"),
    ASSIGN("="), SEMI(";"), COMMA(","), DOT("."),
    LS("("), RS(")"),
    LM("["), RM("]"),
    LB("{"), RB("}");

    private String value;

    Tag(String tag) {
        this.value = tag;
    }

    public String getValue() {
        return this.getValue();
    }

    public static Tag fromString(String tag) {
        for (Tag t : Tag.values()) {
            if (t.value.equals(tag))
                return t;
        }
        return null;
    }
}