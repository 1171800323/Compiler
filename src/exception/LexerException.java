package exception;

public class LexerException extends Exception{

    private static final long serialVersionUID = 1L;
    private String line;    // 记录出错的一行
    private int row;        // 记录出错的行号
    private int column;     // 记录出错的列号

    public LexerException(String line, int row, int column){
        this.line = line;
        this.row = row;
        this.column = column;
    }
}