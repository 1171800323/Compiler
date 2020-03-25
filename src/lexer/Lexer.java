package lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final List<Row> lines = new ArrayList<>();
    private final List<Token> tokens = new ArrayList<>();
    private final Graph graph = new Graph("src/dfa.txt");

    public Lexer(String filename) {
        readFile(filename);
        findTokens();
    }

    private void findTokens() {
        int s = 1;
        for (Row row : lines) {
            String string = row.getLine();
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                s = graph.getTarget(s, c);
                System.out.println(s);
                Map<Integer, Tag> map = graph.getEndStates();
                if (map.keySet().contains(s)){
                    tokens.add(new Word("INT",map.get(s)));
                    s = 1;
                }
            }
        }
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