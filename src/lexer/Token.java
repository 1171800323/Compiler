package lexer;

public class Token {
    // 种别码
    public final Tag tag;

    public Token(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "<"+tag+", _>";
    }
}
