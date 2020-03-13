package lexer;

public class Token {
    // 种别码
    public final int tag;

    public Token(int tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return String.valueOf(tag);
    }
}
