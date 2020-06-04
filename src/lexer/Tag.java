package lexer;

public enum Tag {
    ID("id"), NOTE("note"),
    INT("int"), FLOAT("float"), CHAR("char"), STRUCT("struct"), BOOL("bool"),
    TRUE("true"), FALSE("false"),
    NUM("num"), REAL("real"), WORDS("words"), CHARACTER("character"),
    OCT("oct"), HEX("hex"),
    IF("if"), ELSE("else"), DO("do"), WHILE("while"), FOR("for"), BREAK("break"), CONTINUE("continue"),
    PROC("proc"), CALL("call"), RETURN("return"),THEN("then"),
    AND("&&"), OR("||"), NOT("!"),
    EQ("=="), NE("!="), LE("<="), GE(">="), GT(">"), LT("<"),
    ADD("+"), SUB("-"), MUL("*"), DIV("/"), REMAIN("%"),
    INC("++"), DEC("--"),
    ASSIGN("="), SEMI(";"), COMMA(","), DOT("."),
    LS("("), RS(")"),
    LM("["), RM("]"),
    LB("{"), RB("}"),
    STACK_BOTTOM("$");

    private String value;

    Tag(String tag) {
        this.value = tag;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据接受的字符串返回其对应种别码，如参数tag为"int",则返回Tag.INT
     * @param tag 接收的单词
     * @return 种别码
     */
    public static Tag fromString(String tag) {
        for (Tag t : Tag.values()) {
            if (t.value.equals(tag))
                return t;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(THEN.value);
    }
}