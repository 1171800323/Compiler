package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private final Table lrTable;
    public static final String shiftSymbol = "shift";
    public static final String reduceSymbol = "reduce";

    public Parser() {
        LrTable table = new LrTable("src/parser/grammar.txt");
        lrTable = table.getLrTable();

//        List<String> tokens = new ArrayList<>();
//        tokens.add("id");
//        tokens.add("[");
//        tokens.add("num");
//        tokens.add("]");
//        tokens.add("=");
//        tokens.add("num");
//        tokens.add(";");
//        tokens.add("$");
//        handle(tokens);
        // 这个测试用例存在问题，问题根源在于空串的处理
        List<String> tokens1 = new ArrayList<>();
        tokens1.add("int");
        tokens1.add("id");
        tokens1.add("=");
        tokens1.add("real");
        tokens1.add(";");
        tokens1.add("$");
        handle(tokens1);
    }

    private void handle(List<String> tokens) {
        final Stack<Integer> statusStack = new Stack<>();
        final Stack<String> symbolStack = new Stack<>();
        statusStack.push(0);
        symbolStack.push(LrTable.stackBottom);
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            Action action = lrTable.getAction(statusStack.peek(), token);
            if (statusStack.size() != symbolStack.size()) {
                action = lrTable.getAction(statusStack.peek(), symbolStack.peek());
            }
            if (action != null) {
                if (shiftSymbol.equals(action.getAction())) {
                    System.out.println(action);
                    statusStack.push(action.getStatus());
                    symbolStack.push(token);
                } else if (reduceSymbol.equals(action.getAction())) {
                    Production production = action.getProduction();
                    int num = production.getRight().size();
                    for (int j = 0; j < num; j++) {
                        statusStack.pop();
                        symbolStack.pop();
                    }
                    symbolStack.push(production.getLeft());
                    i--;
                    System.out.println(action);
                } else if (LrTable.acceptSymbol.equals(action.getAction())) {
                    System.out.println("acc");
                    break;
                } else {
                    statusStack.push(action.getStatus());
                    i--;
                }
            }
        }
    }

    public static void main(String[] args) {

        new Parser();
    }
}
