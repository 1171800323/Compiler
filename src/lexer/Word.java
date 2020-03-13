package lexer;

public class Word extends Token {
    public final String lexeme;

    public Word(String lexeme, int tag) {
        super(tag);
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return lexeme;
    }
}
