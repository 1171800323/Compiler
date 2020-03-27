package lexer;

/**
 * 浮点数、科学计数法使用
 */
public class Real extends Token {
    public final float value;

    public Real(float value) {
        super(Tag.REAL);
        this.value = value;
    }

    @Override
    public String toString() {
        return "<" + this.tag + ", " + value + ">";
    }
}
