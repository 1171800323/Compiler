package lexer;

/**
 * 浮点数、科学计数法使用
 */
public class Real extends Token {
    public final double value;

    public Real(double value) {
        super(Tag.REAL);
        this.value = value;
    }

    @Override
    public String toString() {
        return "<" + this.tag + ", " + value + ">";
    }
}
