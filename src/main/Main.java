package main;

import lexer.Lexer;
import lexer.Row;

public class Main {
    public static void main(String[] args) {
        String filename = "test/ex1.txt";
        Lexer lexer = new Lexer(filename);
        for (Row row : lexer.getLines()) {
            System.out.println(row.toString());
        }
    }
}
