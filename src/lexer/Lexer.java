package lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final List<Row> lines = new ArrayList<>();
    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String filename) {
        readFile(filename);
        findTokens();
    }

    private void findTokens() {
        for (Row row : lines) {
            String string = row.getLine();
            for (int i = 0; i < string.length(); i++){
                char c = string.charAt(i);
                System.out.println(c);
            }
        }
//        tokens.add(new Num(1));
//        tokens.add(new Real((float) 2.1));
//        tokens.add(new Word("num", Tag.ID));
//        tokens.add(new Token(Tag.SUB));
//        tokens.add(new Token(Tag.INC));
    }

    private void readFile(String filename) {
        String str;
        int line = 0;
        try (FileInputStream inputStream = new FileInputStream(filename);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((str = bufferedReader.readLine()) != null) {
                line++;
                lines.add(new Row(line, str));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Row> getLines() {
        return this.lines;
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer("test/ex1.txt");
        for (Token token : lexer.getTokens()) {
            System.out.println(token.toString());
        }
    }
}