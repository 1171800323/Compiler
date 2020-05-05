package parser;

import lexer.Lexer;
import lexer.Num;
import lexer.Tag;
import lexer.Token;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


public class Parser {
    public static final String shiftSymbol = "shift";
    public static final String reduceSymbol = "reduce";
    public final LrTable lrtable = new LrTable("src/parser/grammar_semantic.txt");
    private final Table table;

    private final ArrayList<DefaultMutableTreeNode> TreeList = new ArrayList<>();

    // 状态栈和符号栈
    private final Stack<Integer> statusStack = new Stack<>();
    private final Stack<Symbol> symbolStack = new Stack<>();

    // Token序列
    private final List<Token> tokens;

    public Parser(String filename) {
        // 初始化栈
        statusStack.push(0);
        symbolStack.push(new Symbol(LrTable.stackBottom));

        // 词法分析
        Lexer lexer = new Lexer(filename);
        table = lrtable.getLrTable();
        tokens = tokenChange(lexer.getTokens());

        // 语法分析
        handle();
    }

    private void handle() {
        // 一遍扫描
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i).getTag().getValue();
            if (statusStack.empty()) {
                return;
            }
            // 查ACTION表，根据当前状态栈顶符号和token确定动作
            Action action = table.getAction(statusStack.peek(), token);
            if (statusStack.size() != symbolStack.size()) {
                // 当状态栈和符号栈数目不同，需要查GOTO表
                action = table.getAction(statusStack.peek(), symbolStack.peek().getName());
            }
            if (action != null) {
                // 移入动作，同时将token值和状态号进栈
                if (shiftSymbol.equals(action.getAction())) {
                    statusStack.push(action.getStatus());
                    symbolStack.push(new Symbol(token));
                    TreeList.add(new DefaultMutableTreeNode(token));
                    System.out.println(action);
                } else if (reduceSymbol.equals(action.getAction())) {
                    // 规约动作，同时弹出两个栈中内容，最后需要将产生式左部进栈
                    Production production = action.getProduction();

                    // 语法树
                    paintTree(production);

                    int num = production.getRight().size();
                    // 空产生式不需弹栈
                    if (production.isEmptyProduction()) {
                        num = 0;
                    }
                    for (int j = 0; j < num; j++) {
                        statusStack.pop();
                        symbolStack.pop();
                    }
                    symbolStack.push(new Symbol(production.getLeft()));
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
            } else {
                // 错误处理
                parserErrorHandle(i);
            }
        }
    }


    /**
     * 删除注释，将OCT和HEX转化为NUM，增加栈底符号
     */
    public List<Token> tokenChange(List<Token> tokens) {
        List<Token> list = new ArrayList<>();
        for (Token token : tokens) {
            if (token.getTag() == Tag.OCT || token.getTag() == Tag.HEX) {
                Num num = (Num) token;
                list.add(new Num(num.getValue(), Tag.NUM, num.getLine()));
            } else if (token.getTag() != Tag.NOTE) {
                list.add(token);
            }
        }
        list.add(new Token(Tag.STACK_BOTTOM, -1));
        return list;
    }

    private void paintTree(Production production) {
        DefaultMutableTreeNode lord = new DefaultMutableTreeNode(production.getLeft());
        if (production.isEmptyProduction())
            lord.add(new DefaultMutableTreeNode("ε"));
        ArrayList<DefaultMutableTreeNode> childList = new ArrayList<>();
        for (int n1 = production.getRight().size() - 1; n1 >= 0; n1--) {
            for (int n2 = TreeList.size() - 1; n2 >= 0; n2--) {
                if (TreeList.get(n2).toString().equals(production.getRight().get(n1))) {
                    childList.add(TreeList.get(n2));
                    TreeList.remove(n2);
                    break;
                }
            }
        }
        Collections.reverse(childList);
        for (DefaultMutableTreeNode e : childList) {
            lord.add(e);
        }
        TreeList.add(lord);
    }

    public DefaultMutableTreeNode getRoot() {
        return this.TreeList.get(0);
    }

    private void parserErrorHandle(int i) {
        System.out.println("Error token: " + tokens.get(i).toString());
    }

    public static void main(String[] args) {
        new Parser("test/right.txt");
    }
}
