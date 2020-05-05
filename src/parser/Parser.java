package parser;

import lexer.Lexer;
import lexer.Num;
import lexer.Tag;
import lexer.Token;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;


public class Parser {
    public static final String shiftSymbol = "shift";
    public static final String reduceSymbol = "reduce";
    public final LrTable lrtable = new LrTable("src/parser/grammar_semantic.txt");
    private final Table table;
    private final Map<String, Set<String>> followSetMap;
    public static List<Integer> lineno = new ArrayList<>(); //保存每一个的行号
    private final List<String> errorMessages = new ArrayList<>();

    private final ArrayList<DefaultMutableTreeNode> TreeList = new ArrayList<>();

    // 状态栈和符号栈
    private final Stack<Integer> statusStack = new Stack<>();
    private final Stack<Symbol> symbolStack = new Stack<>();

    private final List<Token> tokens;

    public Parser(String filename) {
        // 初始化栈
        statusStack.push(0);
        symbolStack.push(new Symbol(LrTable.stackBottom));

        // 词法分析
        Lexer lexer = new Lexer(filename);
        table = lrtable.getLrTable();
        lineno = lexer.getLineno();  //这一步是获得对应的行号
        tokens = tokenChange(lexer.getTokens());  //获得解析后的tokens

        // 语法分析
        followSetMap = lrtable.getFollowSet();
        handle(tokens);

        // 错误信息
        for (String err : errorMessages) {
            System.err.println(err);
        }
    }

    private void handle(List<Token> tokens) {
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
                //错误处理
//                i = parserErrorHandle(token, i);
            }
        }
    }

    /**
     * 将词法分析获得的token，变成语法分析需要的形式token
     */
    public List<Token> tokenChange(List<Token> tokens) {
        List<Token> list = new ArrayList<>();
        for (Token token : tokens) {
            if (token.getTag() == Tag.OCT) {
                Num num = (Num) token;
                list.add(new Num(num.getValue(), Tag.NUM, num.getLine()));
            } else if (token.getTag() == Tag.HEX) {
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
/*
    private int parserErrorHandle(String token, int i) {
//        System.out.println(i);
        if (i == tokens.size() - 1) {
            errorMessages.add("Error at Line [" + "末尾" + "]:  '" + token + "' ");
            return i;
        }
        //错误位置
        int line1 = lineno.get(i - 1);
        int line2 = lineno.get(i);
        if (line1 == line2) {
            errorMessages.add("Error at Line [" + line1 + "]:  '" + token + "' ");
        } else if (line1 < line2) {
            errorMessages.add("Error at Line [" + line1 + "~" + line2 + "]:  " + "  " + token);
        }

        // 赋值语句缺少分号
        if (symbolStack.get(symbolStack.size() - 2).getName().equals("=") && (symbolStack.peek().getName().equals("id") || symbolStack.peek().getName().equals("real") ||
                symbolStack.peek().getName().equals("num") || symbolStack.peek().getName().equals("character")) && line2 > line1) {
            tokens.add(i, ";");
            lineno.add(i, line1);
            i = i - 1;
            return i;
        }
        // 重复
        else if (symbolStack.peek().getName().equals(token) && !token.equals("(") && !token.equals(")") && !token.equals("{") && !token.equals("}")) {
            tokens.remove(i);
            lineno.remove(i);
            i = i - 1;
            return i;
        }
        // 缺少 if 后面左括号
        else if (symbolStack.peek().getName().equals("if") && !token.equals("(")) {
            tokens.add(i, "(");
            lineno.add(i, line1);
            i = i - 1;
            return i;
        }
        // 结构体缺少左大括号
        else if (symbolStack.peek().getName().equals("id") && !token.equals("(") && symbolStack.get(symbolStack.size() - 2).getName().equals("struct")) {
            tokens.add(i, "{");
            lineno.add(i, line1);
            i = i - 1;
            return i;
        }
        // 缺少右括号
        Action action1 = table.getAction(statusStack.peek(), ")");
        if (action1 != null && action1.getAction().equals(reduceSymbol)) {
            tokens.add(i, ")");
            lineno.add(i, line1);
            i = i - 1;
            return i;
        }
        // 缺少右大括号
        Action action2 = table.getAction(statusStack.peek(), "}");
        if (action2 != null && action2.getAction().equals(reduceSymbol)) {
            tokens.add(i, "}");
            lineno.add(i, line1);
            i = i - 1;
            return i;
        }
        // 缺少右中括号
        Action action3 = table.getAction(statusStack.peek(), "]");
        if (action3 != null && action3.getAction().equals(reduceSymbol)) {
            tokens.add(i, "]");
            lineno.add(i, line1);
            i = i - 1;
            return i;
        }

        //如果不满足常见的几种错误
        int state = statusStack.peek();
        Set<String> nonTerminals = lrtable.getNonTerminals(); //非终结符集合

        //如果是空，说明这个项集里面全是规约状态，接着往前找
        if (lrtable.getGraph().get(state) == null) {
            statusStack.pop();
            symbolStack.pop();
            state = statusStack.peek();//state永远指向栈顶
        }

        // 进行操作，看看是否含有非中介符。前一个set会保留交集，第二个set不变
        nonTerminals.retainAll(lrtable.getGraph().get(state).keySet());
        while (nonTerminals.isEmpty()) {
            //当GOTO表中没有这个可去的状态时，也就是终结符交集是空,接着往前找
            statusStack.pop();
            symbolStack.pop();

            if (statusStack.empty()) {
                break;
            }

            state = statusStack.peek();
            nonTerminals = lrtable.getNonTerminals();
            nonTerminals.retainAll(lrtable.getGraph().get(state).keySet());
        }

        //到这里来，找到一个状态，他的非终结符集合交集不空了，
        String A;
        int nextState;
        Map<String, Integer> map = lrtable.getGraph().get(state);
        for (String s : nonTerminals) {
            A = s;
            int j = i;
            nextState = map.get(A);
            boolean isflag = false;

            while (j < i + 3) {
                statusStack.push(nextState);
                symbolStack.push(new Symbol(A));
                if (j >= tokens.size()) {
                    return i;
                }
                if (followSetMap.get(A).contains(tokens.get(j))) {
                    isflag = true;
                    break;
                } else {
                    statusStack.pop();
                    symbolStack.pop();
                    j++;
                }
            }
            if (isflag) {
                i = i + (j - i) - 1;
                break;
            } else {
                //这个不符合条件，就把刚才加进去的再出去.然后开启下次循环
                statusStack.pop();
                symbolStack.pop();
            }
        }
        return i;
    }
*/
    public static void main(String[] args) {
        new Parser("test/right.txt");
    }
}
