package lexer;

/**
 * 十进制数、八进制、十六进制数使用
 */
public class Num extends Token {
    public final int value;

    public Num(int value,Tag tag) {
        super(tag);
        this.value = value;
    }
    @Override
    public String toString() {
        return "<" + this.tag + ", " + value + ">";
    }
}