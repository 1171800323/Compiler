package lexer;

/**
 * 十进制数、八进制、十六进制数使用
 */
public class Num extends Token {
    private final int value;

    public Num(int value, Tag tag, int line) {
        super(tag, line);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "<" + this.getTag() + ", " + value + ">";
    }
}