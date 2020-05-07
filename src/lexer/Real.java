package lexer;

/**
 * 浮点数、科学计数法使用
 */
public class Real extends Token {
    private final double value;

    public Real(double value, int line) {
        super(Tag.REAL, line);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "<" + this.getTag() + ", " + value + ">";
    }
}
