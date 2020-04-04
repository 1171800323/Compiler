package exception;

/**
 * 错误处理，记录错误信息
 */
public class LexerException extends Exception {

    private static final long serialVersionUID = 1L;
    private String token;    // 记录出错的单词
    private int row;        // 记录出错的行号
    private int column;     // 记录出错的列号

    public LexerException(String token, int row, int column) {
        this.token = token;
        this.row = row;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return "(" + row + "," + column + ")" + ": " + token;
    }
}