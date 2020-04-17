package parser;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


public class Parser {

    private final Table lrTable;
    public static final String shiftSymbol = "shift";
    public static final String reduceSymbol = "reduce";

    private final ArrayList<DefaultMutableTreeNode> TreeList = new ArrayList<>();


    public Parser() {
//        LrTable table = new LrTable("src/parser/grammar_test.txt");
        LrTable table = new LrTable("src/parser/grammar.txt");
        lrTable = table.getLrTable();

        List<String> tokens = new ArrayList<>();
        tokens.add("id");
        tokens.add("[");
        tokens.add("num");
        tokens.add("]");
        tokens.add("=");
        tokens.add("num");
        tokens.add(";");
        tokens.add("int");
        tokens.add("id");
        tokens.add("=");
        tokens.add("real");
        tokens.add(";");
        tokens.add("return");
        tokens.add("real");
        tokens.add(";");
        tokens.add("$");
        handle(tokens);
    }

    private void handle(List<String> tokens) {

        // 状态栈和符号栈
        final Stack<Integer> statusStack = new Stack<>();
        final Stack<String> symbolStack = new Stack<>();
        statusStack.push(0);
        symbolStack.push(LrTable.stackBottom);

        // 一遍扫描
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            // 查ACTION表，根据当前状态栈顶符号和token确定动作
            Action action = lrTable.getAction(statusStack.peek(), token);
            if (statusStack.size() != symbolStack.size()) {
                // 当状态栈和符号栈数目不同，需要查GOTO表
                action = lrTable.getAction(statusStack.peek(), symbolStack.peek());
            }
            if (action != null) {
                // 移入动作，同时将token值和状态号进栈
                if (shiftSymbol.equals(action.getAction())) {
                    statusStack.push(action.getStatus());
                    symbolStack.push(token);
                    TreeList.add(new DefaultMutableTreeNode(token));
                    System.out.println(action);
                } else if (reduceSymbol.equals(action.getAction())) {
                    // 规约动作，同时弹出两个栈中内容，最后需要将产生式左部进栈
                    Production production = action.getProduction();
                    int num = production.getRight().size();
                    DefaultMutableTreeNode lord = new DefaultMutableTreeNode(production.getLeft());    
                    if(production.isEmptyProduction())
                        lord.add(new DefaultMutableTreeNode("ε"));
                    ArrayList<DefaultMutableTreeNode> childlist = new ArrayList<>();
                    for(int n1=production.getRight().size()-1 ; n1>=0 ; n1--) {
                        for(int n2=TreeList.size()-1 ; n2>=0 ; n2--){
                            if(TreeList.get(n2).toString().equals(production.getRight().get(n1)))
                            {
                                childlist.add(TreeList.get(n2));
                                TreeList.remove(n2);
                                break;
                            }
                        }
                    }
                    Collections.reverse(childlist);
                    for(DefaultMutableTreeNode e : childlist)
                    {
                        lord.add(e);
                    }
                    TreeList.add(lord);
                    
                    // 空产生式不需弹栈
                    if (production.isEmptyProduction()) {
                        num = 0;
                    }
                    for (int j = 0; j < num; j++) {
                        statusStack.pop();
                        symbolStack.pop();
                    }
                    symbolStack.push(production.getLeft());
                    // 指针不移动
                    i--;
                    System.out.println(action);
                } else if (LrTable.acceptSymbol.equals(action.getAction())) {
                    // 接收，直接退出
                    System.out.println("acc");
                    break;
                } else {
                    // 其他情况，即两个栈符号数目不同时，需要查GOTO表，将状态号进栈
                    statusStack.push(action.getStatus());
                    // 指针不移动
                    i--;
                }
            }
        }
    }

    public DefaultMutableTreeNode getRoot() {
        return this.TreeList.get(0);
    }

    public static void main(String[] args) {

        new Parser();
    }
}
