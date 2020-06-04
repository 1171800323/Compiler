package lexer;

/**
 * 运算符、界符等一词一码者使用
 */
public class Token {
    // 种别码
    private final Tag tag;
    private final int line;

    public Token(Tag tag, int line) {
        this.tag = tag;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "<" + tag + ", _>";
    }
}
