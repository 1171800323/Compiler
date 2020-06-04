package lexer;

/**
 * 注释、ID、关键字、char字符使用
 */
public class Word extends Token {
    private final String lexeme;

    public Word(String lexeme, Tag tag, int line) {
        super(tag, line);
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "<" + this.getTag() + ", " + lexeme + ">";
    }
}