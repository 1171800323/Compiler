package parser;

import lexer.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;


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

    // 符号表
    private final Map<String, SymbolItem> symbolTable = new HashMap<>();
    private int offset = 0;

    // 中间代码
    private final List<IntermediateCode> codeList = new ArrayList<>();
    // 代码编号
    private int nextQuad = 0;

    // 临时变量
    private final String temp = "t";
    private int tempCount = 1;

    // 中间值
    private String tBridging = "";
    private String wBridging = "";

    // 错误信息
    private final List<String> semanticErrorMessage = new ArrayList<>();

    public Parser(String filename) {
        // 初始化栈
        statusStack.push(0);
        symbolStack.push(new Symbol(LrTable.stackBottom));

        // 词法分析
        Lexer lexer = new Lexer(filename);
        tokens = tokenChange(lexer.getTokens());

        // 语法分析和语义分析
        table = lrtable.getLrTable();
        handle();

        // 打印符号表
        printSymbolTable();
    }


    private void handle() {
        // 一遍扫描
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String grammarSymbol = token.getTag().getValue();
            if (statusStack.empty()) {
                return;
            }
            // 查ACTION表，根据当前状态栈顶符号和当前文法符号确定动作
            Action action = table.getAction(statusStack.peek(), grammarSymbol);
            if (statusStack.size() != symbolStack.size()) {
                // 当状态栈和符号栈数目不同，需要查GOTO表
                action = table.getAction(statusStack.peek(), symbolStack.peek().getName());
            }
            if (action != null) {
                // 移入动作，将当前文法符号和状态号进栈
                if (shiftSymbol.equals(action.getAction())) {
                    statusStack.push(action.getStatus());
                    Symbol symbol = new Symbol(grammarSymbol);
                    if (token.getTag() == Tag.NUM) {
                        symbol.putAttribute("value", String.valueOf(((Num) token).getValue()));
                    } else if (token.getTag() == Tag.REAL) {
                        symbol.putAttribute("value", String.valueOf(((Real) token).getValue()));
                    } else if (token.getTag() == Tag.WORDS || token.getTag() == Tag.ID) {
                        symbol.putAttribute("lexeme", ((Word) token).getLexeme());
                        symbol.putAttribute("lineNum", String.valueOf(token.getLine()));
                    }
                    symbolStack.push(symbol);

                    // 语法树
                    TreeList.add(new DefaultMutableTreeNode(grammarSymbol));

                    System.out.println(action);
                } else if (reduceSymbol.equals(action.getAction())) {
                    // 规约动作，同时弹出两个栈中内容，最后需要将产生式左部进栈
                    Production production = action.getProduction();

                    // 语法树
                    paintTree(production);

                    // 规约时动作
                    reduce(production);

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
                // 语法分析错误处理
                // TODO 语义分析所做的一些修改使得原先错误处理代码不可用，此处遇到错误直接退出，待完善
                parserErrorHandle(i);
                break;
            }
        }
    }


    private int getIntFromString(String s) {
        return Integer.parseInt(s);
    }

    // 规约时语法、语义动作
    private void reduce(Production production) {
        popStatusStack(production);
        if (production.toString().equals("D -> T id ;")) {
            symbolStack.pop();
            Symbol id = symbolStack.pop();
            Symbol T = symbolStack.pop();
            String idLexeme = id.getAttribute("lexeme");
            int idLineNum = getIntFromString(id.getAttribute("lineNum"));
            String tType = T.getAttribute("type");
            int tWidth = getIntFromString(T.getAttribute("width"));
            if (symbolTable.containsKey(idLexeme)) {
                semanticErrorMessage.add("Error at line[" + idLineNum + "]: " + "重复声明的变量名 " + idLexeme);
            } else {
                symbolTable.put(idLexeme, new SymbolItem(idLexeme, tType, offset, idLineNum));
                offset += tWidth;
            }
            symbolStack.push(new Symbol(production.getLeft()));
        } else if (production.toString().equals("T -> X TM C")) {
            Symbol C = symbolStack.pop();
            symbolStack.pop();
            symbolStack.pop();
            Symbol T = new Symbol(production.getLeft());
            T.putAttribute("type", C.getAttribute("type"));
            T.putAttribute("width", C.getAttribute("width"));
            symbolStack.push(T);
        } else if (production.toString().equals("TM -> " + LrTable.emptySymbol)) {
            Symbol X = symbolStack.peek();
            tBridging = X.getAttribute("type");
            wBridging = X.getAttribute("width");
            symbolStack.push(new Symbol(production.getLeft()));
        } else if (production.toString().equals("X -> int")) {
            symbolStack.pop();
            Symbol X = new Symbol(production.getLeft());
            X.putAttribute("type", "int");
            X.putAttribute("width", "4");
            symbolStack.push(X);
        } else if (production.toString().equals("X -> float")) {
            symbolStack.pop();
            Symbol X = new Symbol(production.getLeft());
            X.putAttribute("type", "float");
            X.putAttribute("width", "8");
            symbolStack.push(X);
        } else if (production.toString().equals("X -> char")) {
            symbolStack.pop();
            Symbol X = new Symbol(production.getLeft());
            X.putAttribute("type", "char");
            X.putAttribute("width", "1");
            symbolStack.push(X);
        } else if (production.toString().equals("C -> [ num ] C")) {
            Symbol C1 = symbolStack.pop();
            symbolStack.pop();
            Symbol num = symbolStack.pop();
            symbolStack.pop();
            String numVal = num.getAttribute("value");
            String c1Type = C1.getAttribute("type");
            String c1Width = C1.getAttribute("width");
            Symbol C = new Symbol(production.getLeft());
            int cWidth = getIntFromString(numVal) * getIntFromString(c1Width);
            C.putAttribute("type", "array(" + numVal + ", " + c1Type + ")");
            C.putAttribute("width", String.valueOf(cWidth));
            symbolStack.push(C);
        } else if (production.toString().equals("C -> " + LrTable.emptySymbol)) {
            Symbol C = new Symbol(production.getLeft());
            C.putAttribute("type", tBridging);
            C.putAttribute("width", wBridging);
            symbolStack.push(C);
        } else if (production.toString().equals("D -> struct id DM1 { P }")
                || production.toString().equals("D -> proc X id DM2 ( M ) { P }")) {
            popSymbolStack(production);
            symbolStack.push(new Symbol(production.getLeft()));
        } else if (production.toString().equals("DM1 -> " + LrTable.emptySymbol)) {
            Symbol DM1 = new Symbol(production.getLeft());
            Symbol id = symbolStack.peek();
            String idLexeme = id.getAttribute("lexeme");
            String type = "record";
            int lineNum = getIntFromString(id.getAttribute("lineNum"));
            if (symbolTable.containsKey(idLexeme)) {
                semanticErrorMessage.add("Error at line [" + lineNum + "]: " + "重复的记录声明 " + idLexeme);
            } else {
                symbolTable.put(idLexeme, new SymbolItem(idLexeme, type, offset, lineNum));
            }
            symbolStack.push(DM1);
        } else if (production.toString().equals("DM2 -> " + LrTable.emptySymbol)) {
            Symbol DM2 = new Symbol(production.getLeft());
            Symbol id = symbolStack.peek();
            String idLexeme = id.getAttribute("lexeme");
            String type = "proc";
            int lineNum = getIntFromString(id.getAttribute("lineNum"));
            if (symbolTable.containsKey(idLexeme)) {
                semanticErrorMessage.add("Error at line [" + lineNum + "]: " + "重复的过程声明 " + idLexeme);
            } else {
                symbolTable.put(idLexeme, new SymbolItem(idLexeme, type, offset, lineNum));
            }
            symbolStack.push(DM2);
        } else {
            popSymbolStack(production);
            symbolStack.push(new Symbol(production.getLeft()));
        }
    }

    // 状态栈弹栈
    private void popStatusStack(Production production) {
        int num = production.getRight().size();
        // 空产生式不需弹栈
        if (production.isEmptyProduction()) {
            num = 0;
        }
        for (int i = 0; i < num; i++) {
            statusStack.pop();
        }
    }

    private void popSymbolStack(Production production) {
        int num = production.getRight().size();
        // 空产生式不需弹栈
        if (production.isEmptyProduction()) {
            num = 0;
        }
        for (int i = 0; i < num; i++) {
            symbolStack.pop();
        }
    }

    private void printSymbolTable() {
        System.out.println("Symbol Table: ");
        List<SymbolItem> list = new ArrayList<>();
        list.addAll(symbolTable.values());
        list.sort(Comparator.comparingInt(SymbolItem::getLineNum));
        for (SymbolItem symbolItem : list) {
            System.out.println(symbolItem.toString());
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

    // 语法分析错误处理
    // TODO 语义分析所做的一些修改使得原先错误处理代码不可用，此处为简单版本（仅打印错误之处），待完善
    private void parserErrorHandle(int i) {
        Token token = tokens.get(i);
        System.out.println("Error at line: " + token.getLine() +
                " [" + tokens.get(i).toString() + "]");
    }

    public static void main(String[] args) {
        new Parser("test/test.txt");
    }
}