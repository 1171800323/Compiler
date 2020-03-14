package lexer;

public class Row {
    private final int num;
    private final String line;

    public Row(int num, String line) {
        this.num = num;
        this.line = line;
    }

    public int getNum() {
        return this.num;
    }

    public String getLine() {
        return this.line;
    }

    @Override
    public String toString() {
        return num + ": " + line;
    }
}