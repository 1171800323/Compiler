package parser;

public class Parser {
    private LrTable lrTable = new LrTable("src/parser/grammar.txt");

    public Parser(String filename) {

    }

    public static void main(String[] args) {
        Parser parser = new Parser("src/parser/grammar.txt");
    }
}
