package parser;


import lexer.*;


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

    // 符号表
    private final SymbolTable symbolTable = new SymbolTable();
    private int offset = 0;

    // 中间代码
    private final CodeList codeList = new CodeList();

    // 四元式序列
    private  String quaternions ;

    // 记录临时变量t使用的数目
    private int tempCount = 1;

    // 中间值
    private String tBridging = "";
    private String wBridging = "";

    // 过程调用语句参数
    private final List<String> callParams = new ArrayList<>();

    // 错误信息
    private final SemanticErrorMessage semanticErrorMessage = new SemanticErrorMessage();

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
        System.out.println("符号表: ");
        System.out.println(symbolTable.toString());

        // 打印中间代码
        System.out.println("中间代码: ");
        System.out.println(codeList.toString());

        // 打印四元式序列
        quaternions = codeList.getQuaternions().toString() ;
        System.out.println("\n四元式序列: ");
        System.out.println(quaternions);


        // 打印语义分析错误信息
        System.err.println("\t语义分析错误信息: ");
        System.err.println(semanticErrorMessage.toString());
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
                    } else if (token.getTag() == Tag.CHARACTER) {
                        symbol.putAttribute("value", ((Word) token).getLexeme());
                    } else if (token.getTag() == Tag.ID) {
                        symbol.putAttribute("lexeme", ((Word) token).getLexeme());
                        symbol.putAttribute("lineNum", String.valueOf(token.getLine()));
                    }
                    if (symbolStack.peek().getName().equals("S")){
                        Symbol S = symbolStack.peek();
                        codeList.backPatch(S.getList("next"), String.valueOf(codeList.getQuad()));
                    }
                    symbolStack.push(symbol);

                    // 语法树
                    TreeList.add(new DefaultMutableTreeNode(grammarSymbol));

                    System.out.println(action);
                } else if (reduceSymbol.equals(action.getAction())) {
                    // 规约动作，同时弹出两个栈中内容，最后需要将产生式左部进栈
                    Production production = action.getProduction();
                    //执行动作

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
                parserErrorHandle( i );
                break;
            }
        }
    }


    private int getIntFromString(String s) {
        return Integer.parseInt(s);
    }

    private String newTemp() {
        String temp = "t" + tempCount;
        tempCount += 1;
        return temp;
    }

    private String getLTypeElem(String lType) {
        int commaIndex = lType.indexOf(',');
        int length = lType.length();
        return lType.substring(commaIndex + 1, length - 1).trim();
    }

    private int getLTypeWidth(String type) {
        int width = 1;
        switch (type) {
            case "int":
                width = 4;
                break;
            case "float":
                width = 8;
                break;
            case "char":
                width = 1;
                break;
            default:
                for (String s : type.split(",\\s*")) {
                    int temp = s.indexOf("(");
                    if (temp != -1) {
                        int tempWidth = getIntFromString(s.substring(temp + 1));
                        width *= tempWidth;
                    } else {
                        String elemType = s.substring(0, s.indexOf(")"));
                        switch (elemType) {
                            case "int":
                                width *= 4;
                                break;
                            case "float":
                                width *= 8;
                                break;
                            case "char":
                                width *= 1;
                                break;
                        }
                    }
                }
        }
        return width;
    }

    private List<Integer> makeList(int quad) {
        List<Integer> list = new ArrayList<>();
        list.add(quad);
        return list;
    }

    // 规约时语法、语义动作
    private void reduce(Production production) {
        popStatusStack(production);
        switch (production.toString()) {
            case "D -> T id ;": {
                symbolStack.pop();
                Symbol id = symbolStack.pop();
                Symbol T = symbolStack.pop();
                String idLexeme = id.getAttribute("lexeme");
                int idLineNum = getIntFromString(id.getAttribute("lineNum"));
                String tType = T.getAttribute("type");
                int tWidth = getIntFromString(T.getAttribute("width"));
                if (symbolTable.isIdExisted(idLexeme)) {
                    semanticErrorMessage.add(idLineNum, "重复声明的变量名 " + idLexeme);
                } else {
                    symbolTable.put(idLexeme, new SymbolItem(idLexeme, tType, offset, idLineNum));
                    offset += tWidth;
                }
                symbolStack.push(new Symbol(production.getLeft()));
                break;
            }
            case "T -> X TM C": {
                Symbol C = symbolStack.pop();
                symbolStack.pop();
                symbolStack.pop();
                Symbol T = new Symbol(production.getLeft());
                T.putAttribute("type", C.getAttribute("type"));
                T.putAttribute("width", C.getAttribute("width"));
                symbolStack.push(T);
                break;
            }
            case "TM -> " + LrTable.emptySymbol: {
                Symbol X = symbolStack.peek();
                tBridging = X.getAttribute("type");
                wBridging = X.getAttribute("width");
                symbolStack.push(new Symbol(production.getLeft()));
                break;
            }
            case "X -> int":
            case "X -> float":
            case "X -> char": {
                symbolStack.pop();
                Symbol X = new Symbol(production.getLeft());
                String right = production.getRight().get(0);
                String width = "4";
                if (right.equals("float")) {
                    width = "8";
                } else if (right.equals("char")) {
                    width = "1";
                }
                X.putAttribute("type", right);
                X.putAttribute("width", width);
                symbolStack.push(X);
                break;
            }
            case "C -> [ num ] C": {
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
                break;
            }
            case "C -> " + LrTable.emptySymbol: {
                Symbol C = new Symbol(production.getLeft());
                C.putAttribute("type", tBridging);
                C.putAttribute("width", wBridging);
                symbolStack.push(C);
                break;
            }
            case "DM1 -> " + LrTable.emptySymbol:
            case "DM2 -> " + LrTable.emptySymbol: {
                Symbol DM = new Symbol(production.getLeft());
                Symbol id = symbolStack.peek();
                String idLexeme = id.getAttribute("lexeme");
                String type, errorReason;
                if (production.getLeft().equals("DM1")) {
                    type = "record";
                    errorReason = "重复的记录声明";
                } else {
                    type = "proc";
                    errorReason = "重复的过程声明";
                }
                int lineNum = getIntFromString(id.getAttribute("lineNum"));
                if (symbolTable.isIdExisted(idLexeme)) {
                    semanticErrorMessage.add(lineNum, errorReason + idLexeme);
                } else {
                    symbolTable.put(idLexeme, new SymbolItem(idLexeme, type, offset, lineNum));
                }
                symbolStack.push(DM);
                break;
            }
            case "M -> M , X id": {
                Symbol id = symbolStack.pop();
                Symbol X = symbolStack.pop();
                symbolStack.pop();
                Symbol M1 = symbolStack.pop();
                String idLexeme = id.getAttribute("lexeme");
                String xType = X.getAttribute("type");
                int xWidth = getIntFromString(X.getAttribute("width"));
                int lineNum = getIntFromString(id.getAttribute("lineNum"));
                if (symbolTable.isIdExisted(idLexeme)) {
                    semanticErrorMessage.add(lineNum, "重复声明的变量名 " + idLexeme);
                } else {
                    symbolTable.put(idLexeme, new SymbolItem(idLexeme, xType, offset, lineNum));
                    offset += xWidth;
                }
                Symbol M = new Symbol(production.getLeft());
                int mSize = getIntFromString(M1.getAttribute("size")) + 1;
                M.putAttribute("size", String.valueOf(mSize));
                symbolStack.push(M);
                break;
            }
            case "M -> X id": {
                Symbol id = symbolStack.pop();
                Symbol X = symbolStack.pop();
                String idLexeme = id.getAttribute("lexeme");
                String xType = X.getAttribute("type");
                int xWidth = getIntFromString(X.getAttribute("width"));
                int lineNum = getIntFromString(id.getAttribute("lineNum"));
                if (symbolTable.isIdExisted(idLexeme)) {
                    semanticErrorMessage.add(lineNum, "重复声明的变量名 " + idLexeme);
                } else {
                    symbolTable.put(idLexeme, new SymbolItem(idLexeme, xType, offset, lineNum));
                    offset += xWidth;
                }
                Symbol M = new Symbol(production.getLeft());
                M.putAttribute("size", "1");
                symbolStack.push(M);
                break;
            }
            case "L -> L [ E ]": {
                symbolStack.pop();
                Symbol E = symbolStack.pop();
                symbolStack.pop();
                Symbol L1 = symbolStack.pop();

                String eAddr = E.getAttribute("addr");
                String l1Array = L1.getAttribute("array");
                String l1Type = L1.getAttribute("type");
                String l1Offset = L1.getAttribute("offset");

                Symbol L = new Symbol(production.getLeft());
                String lType = getLTypeElem(l1Type);
                int lTypeWidth = getLTypeWidth(lType);
                L.putAttribute("array", l1Array);
                L.putAttribute("type", lType);
                String t = newTemp();

                if(eAddr.contains(".") ){
                    semanticErrorMessage.add(getIntFromString(L1.getAttribute("lineNum")), "数组下标不是整数: "+eAddr);
                }



                codeList.addCode(new String[]{t, "=", eAddr, "*", String.valueOf(lTypeWidth)});
                String lOffset = newTemp();
                L.putAttribute("offset", lOffset);
                codeList.addCode(new String[]{lOffset, "=", l1Offset, "+", t});
                symbolStack.push(L);
                break;



            }
            case "L -> id [ E ]": {
                symbolStack.pop();
                Symbol E = symbolStack.pop();
                symbolStack.pop();
                Symbol id = symbolStack.pop();
                String idLexeme = id.getAttribute("lexeme");
                int idLineNum = getIntFromString(id.getAttribute("lineNum"));

                Symbol L = new Symbol(production.getLeft());
                if (symbolTable.isIdExisted(idLexeme)) {
                    String idType = symbolTable.getType(idLexeme);
                    System.out.println(idType);
                    if (idType.contains("array")) {
                        L.putAttribute("array", idLexeme);
                        String lType = getLTypeElem(idType);
                        L.putAttribute("type", lType);
                        String t = newTemp();
                        L.putAttribute("offset", t);
                        L.putAttribute("lineNum", String.valueOf(idLineNum));
                        String eAddr = E.getAttribute("addr");

                        if(E.getAttribute("addr").contains(".") ){
                            semanticErrorMessage.add(idLineNum, "数组下标不是整数: "+eAddr);
                        }

                        codeList.addCode(new String[]{t, "=", eAddr, "*", String.valueOf(getLTypeWidth(lType))});
                    } else {
                        semanticErrorMessage.add(idLineNum, "非数组类型变量使用了数组操作: " + idLexeme);
                    }
                } else {
                    semanticErrorMessage.add(idLineNum, "未经声明就使用的变量 " + idLexeme);
                }
                symbolStack.push(L);
                break;
            }
            case "E -> E + G": {
                Symbol G = symbolStack.pop();
                symbolStack.pop();
                Symbol E1 = symbolStack.pop();

                String e1Addr = E1.getAttribute("addr");
                String gAddr = G.getAttribute("addr");
                String t = newTemp();
                Symbol E = new Symbol(production.getLeft());
                E.putAttribute("addr", t);

                if(E1.getAttribute("lineNum")!= null){
                    E.putAttribute("lineNum",E1.getAttribute("lineNum"));
                }
                if(G.getAttribute("lineNum")!= null){
                    E.putAttribute("lineNum",G.getAttribute("lineNum"));
                }


                if(E1.getAttribute("type").equals("char") || G.getAttribute("type").equals("char")){
                    int idLineNum = 0 ;
                    if( E1.getAttribute("lineNum")!= null ){
                        idLineNum = getIntFromString(E1.getAttribute("lineNum")) ;
                    }
                    if( G.getAttribute("lineNum")!= null ){
                        idLineNum = getIntFromString(G.getAttribute("lineNum")) ;
                    }
                    semanticErrorMessage.add(0, "运算符与运算分量不匹配: " +e1Addr +" "+"+"+" "+gAddr) ;
                }

                codeList.addCode(new String[]{t, "=", e1Addr, "+", gAddr});
                symbolStack.push(E);
                break;
            }
            case "E -> G":
            case "G -> F": {
                Symbol right = symbolStack.pop();
                Symbol left = new Symbol(production.getLeft());
                left.putAttribute("addr", right.getAttribute("addr"));
                left.putAttribute("type",right.getAttribute("type"));
                left.putAttribute("lineNum",right.getAttribute("lineNum"));
                symbolStack.push(left);
                break;
            }
            case "G -> G * F": {
                Symbol F = symbolStack.pop();
                symbolStack.pop();
                Symbol G1 = symbolStack.pop();
                String t = newTemp();
                Symbol G = new Symbol(production.getLeft());
                G.putAttribute("addr", t);
                if(G1.getAttribute("lineNum")!= null){
                    G.putAttribute("lineNum",G1.getAttribute("lineNum"));
                }
                if(F.getAttribute("lineNum")!= null){
                    G.putAttribute("lineNum",F.getAttribute("lineNum"));
                }
                if(G1.getAttribute("type").equals("char") || F.getAttribute("type").equals("char")
                        || G1.getAttribute("type").contains("array") || F.getAttribute("type").contains("array")){
                    int idLineNum = 0 ;
                    if( G1.getAttribute("lineNum")!= null ){
                        idLineNum = getIntFromString(G.getAttribute("lineNum")) ;
                    }
                    if( F.getAttribute("lineNum")!= null ){
                        idLineNum = getIntFromString(F.getAttribute("lineNum")) ;
                    }
                    semanticErrorMessage.add(0, "运算符与运算分量不匹配: " +G1.getAttribute("addr") +" "+"*"+" "+F.getAttribute("addr")) ;
                }


                codeList.addCode(new String[]{t, "=", G1.getAttribute("addr"), "*", F.getAttribute("addr")});
                symbolStack.push(G);
                break;
            }
            case "F -> ( E )": {
                symbolStack.pop();
                Symbol E = symbolStack.pop();
                symbolStack.pop();
                Symbol F = new Symbol(production.getLeft());
                F.putAttribute("addr", E.getAttribute("addr"));
                F.putAttribute("type", E.getAttribute("type"));
                symbolStack.push(F);
                break;
            }
            case "F -> num":
            case "F -> real":
            case "F -> character": {
                Symbol num = symbolStack.pop();
                String val = num.getAttribute("value");
                Symbol F = new Symbol(production.getLeft());
                F.putAttribute("addr", val);
                String type = production.getRight().get(0);
                if(type.equals("num")){
                    F.putAttribute("type","int");
                }
                else if(type.equals("real")) {
                    F.putAttribute("type","float");
                }
                else if(type.equals("character")) {
                    F.putAttribute("type","char");
                }
                symbolStack.push(F);
                break;
            }
            case "F -> id": {
                Symbol id = symbolStack.pop();
                String idLexeme = id.getAttribute("lexeme");
                int idLineNum = getIntFromString(id.getAttribute("lineNum"));
                Symbol F = new Symbol(production.getLeft());
                if (!symbolTable.isIdExisted(idLexeme)) {
                    semanticErrorMessage.add(idLineNum, "未经声明就使用的变量 " + idLexeme);
                }
                F.putAttribute("addr", idLexeme);
                F.putAttribute("type",symbolTable.getType(id.getName()));
                F.putAttribute("lineNum",id.getAttribute("lineNum"));
                symbolStack.push(F);
                break;
            }
            case "F -> L": {
                Symbol L = symbolStack.pop();
                String lArray = L.getAttribute("array");
                String lOffset = L.getAttribute("offset");
                Symbol F = new Symbol(production.getLeft());
                F.putAttribute("addr", lArray + "[" + lOffset + "]");
                symbolStack.push(F);
                break;
            }
            case "S -> L = E ;": {
                symbolStack.pop();
                Symbol E = symbolStack.pop();
                symbolStack.pop();
                Symbol L = symbolStack.pop();
                codeList.addCode(new String[]{L.getAttribute("array"), "[",
                        L.getAttribute("offset"), "]", "=", E.getAttribute("addr")});
                symbolStack.push(new Symbol(production.getLeft()));
                break;
            }
            case "S -> id = E ;": {
                symbolStack.pop();
                Symbol E = symbolStack.pop();
                symbolStack.pop();
                Symbol id = symbolStack.pop();
                String idLexeme = id.getAttribute("lexeme");
                int idLineNum = getIntFromString(id.getAttribute("lineNum"));
                if (symbolTable.isIdExisted(idLexeme)) {
                    codeList.addCode(new String[]{idLexeme, "=", E.getAttribute("addr")});
                } else {
                    semanticErrorMessage.add(idLineNum, "未经声明就使用的变量 " + idLexeme);
                }
                symbolStack.push(new Symbol(production.getLeft()));
                break;
            }
            case "S -> if ( B ) BM then S N else BM S": {
                Symbol S2 = symbolStack.pop();
                Symbol BM2 = symbolStack.pop();
                symbolStack.pop();
                Symbol N = symbolStack.pop();
                Symbol S1 = symbolStack.pop();
                symbolStack.pop();
                Symbol BM1 = symbolStack.pop();
                symbolStack.pop();
                Symbol B = symbolStack.pop();
                symbolStack.pop();
                symbolStack.pop();
                Symbol S = new Symbol(production.getLeft());
                codeList.backPatch(B.getList("true"), BM1.getAttribute("quad"));
                codeList.backPatch(B.getList("false"), BM2.getAttribute("quad"));
                List<Integer> temp = S.mergeList("next", S1.getList("next")
                        , N.getList("next"));
                S.mergeList("next", temp, S2.getList("next"));

                symbolStack.push(S);
                break;
            }
            case "S -> while BM ( B ) do BM S": {
                Symbol S1 = symbolStack.pop();
                Symbol BM2 = symbolStack.pop();
                symbolStack.pop();
                symbolStack.pop();
                Symbol B = symbolStack.pop();
                symbolStack.pop();
                Symbol BM1 = symbolStack.pop();
                symbolStack.pop();
                Symbol S = new Symbol(production.getLeft());
                codeList.backPatch(S1.getList("next"), BM1.getAttribute("quad"));
                codeList.backPatch(B.getList("true"), BM2.getAttribute("quad"));
                S.addList("next", B.getList("false"));
                codeList.addCode(new String[]{"goto", BM1.getAttribute("quad")});
                symbolStack.push(S);
                break;
            }
            case "Elist -> Elist , E": {
                Symbol E = symbolStack.pop();
                symbolStack.pop();
                Symbol eList1 = symbolStack.pop();
                Symbol eList = new Symbol(production.getLeft());
                int size = getIntFromString(eList1.getAttribute("size")) +1;
                eList.putAttribute("size", String.valueOf(size));
                callParams.add(E.getAttribute("addr"));
                symbolStack.push(eList);
                break;
            }
            case "Elist -> E": {
                Symbol E = symbolStack.pop();
                callParams.clear();
                callParams.add(E.getAttribute("addr"));
                Symbol eList = new Symbol(production.getLeft());
                eList.putAttribute("size" ,"1" );
                symbolStack.push(eList);
                break;
            }
            case "S -> call id ( Elist ) ;": {
                symbolStack.pop();
                symbolStack.pop();
                symbolStack.pop();
                symbolStack.pop();
                Symbol id = symbolStack.pop();
                symbolStack.pop();
                Symbol S = new Symbol(production.getLeft());
                symbolStack.push(S);
                int n = 0;
                for (String t : callParams) {
                    codeList.addCode(new String[]{"param", t});
                    n = n + 1;
                }
                if( symbolTable.isIdExisted(id.getAttribute("lexeme")) ){
                    codeList.addCode(new String[]{"call", id.getAttribute("lexeme"), ",", String.valueOf(n)});

                }
                else {
                    int idLineNum = getIntFromString(id.getAttribute("lineNum"));
                    semanticErrorMessage.add(  idLineNum ,"对普通变量使用了过程调用操作符");
                }
                break;
            }
            case "S -> return E ;": {
                symbolStack.pop();
                Symbol E = symbolStack.pop();
                symbolStack.pop();
                Symbol S = new Symbol(production.getLeft());
                codeList.addCode(new String[]{"return", E.getAttribute("addr")});
                symbolStack.push(S);
                break;
            }
            case "BM -> " + LrTable.emptySymbol: {
                Symbol BM = new Symbol(production.getLeft());
                BM.putAttribute("quad", String.valueOf(codeList.getQuad()));
                symbolStack.push(BM);
                break;
            }
            case "N -> " + LrTable.emptySymbol: {
                Symbol N = new Symbol(production.getLeft());
                N.addList("next", makeList(codeList.getQuad()));
                symbolStack.push(N);
                codeList.addCode(new String[]{"goto"});
                break;
            }
            case "B -> B || BM H": {
                Symbol H = symbolStack.pop();
                Symbol BM = symbolStack.pop();
                symbolStack.pop();
                Symbol B1 = symbolStack.pop();
                Symbol B = new Symbol(production.getLeft());

                B.mergeList("true", B1.getList("true"), H.getList("true"));
                B.addList("false", H.getList("false"));

                codeList.backPatch(B1.getList("false"), BM.getAttribute("quad"));
                symbolStack.push(B);
                break;
            }
            case "B -> H":
            case "H -> I": {
                Symbol right = symbolStack.pop();
                Symbol left = new Symbol(production.getLeft());
                left.addList("true", right.getList("true"));
                left.addList("false", right.getList("false"));
                symbolStack.push(left);
                break;
            }
            case "H -> H && BM I": {
                Symbol I = symbolStack.pop();
                Symbol BM = symbolStack.pop();
                symbolStack.pop();
                Symbol H1 = symbolStack.pop();
                Symbol H = new Symbol(production.getLeft());
                H.addList("true", I.getList("true"));
                H.mergeList("false", H1.getList("false"), I.getList("false"));
                codeList.backPatch(H1.getList("true"), BM.getAttribute("quad"));
                symbolStack.push(H);
                break;
            }
            case "I -> ! I": {
                Symbol I1 = symbolStack.pop();
                symbolStack.pop();
                Symbol I = new Symbol(production.getLeft());
                I.addList("true", I1.getList("false"));
                I.addList("false", I1.getList("true"));
                symbolStack.push(I);
                break;
            }
            case "I -> ( B )": {
                symbolStack.pop();
                Symbol B = symbolStack.pop();
                symbolStack.pop();
                Symbol I = new Symbol(production.getLeft());
                I.addList("true", B.getList("true"));
                I.addList("false", B.getList("false"));
                symbolStack.push(I);
                break;
            }
            case "relop -> <":
            case "relop -> >":
            case "relop -> >=":
            case "relop -> <=":
            case "relop -> !=":
            case "relop -> ==": {
                symbolStack.pop();
                Symbol reLop = new Symbol(production.getLeft());
                reLop.putAttribute("op", production.getRight().get(0));
                symbolStack.push(reLop);
                break;
            }
            case "I -> E relop E": {
                Symbol E2 = symbolStack.pop();
                Symbol reLop = symbolStack.pop();
                Symbol E1 = symbolStack.pop();
                Symbol I = new Symbol(production.getLeft());
                I.addList("true", makeList(codeList.getQuad()));
                I.addList("false", makeList(codeList.getQuad() + 1));
                codeList.addCode(new String[]{"if", E1.getAttribute("addr"), reLop.getAttribute("op")
                        , E2.getAttribute("addr"), "goto"});
                codeList.addCode(new String[]{"goto"});
                symbolStack.push(I);
                break;
            }
            case "I -> true":
            case "I -> false": {
                symbolStack.pop();
                Symbol I = new Symbol(production.getLeft());
                I.addList(production.getRight().get(0), makeList(codeList.getQuad()));
                codeList.addCode(new String[]{"goto"});
                symbolStack.push(I);
                break;
            }
            case "D -> proc X id DM2 ( M ) { P }":{
                symbolStack.pop() ;
                symbolStack.pop() ;
                symbolStack.pop() ;
                symbolStack.pop() ;
                Symbol M = symbolStack.pop() ;
                symbolStack.pop() ;
                symbolStack.pop() ;
                Symbol id = symbolStack.pop() ;
                symbolStack.pop() ;
                symbolStack.pop() ;
                String size = (M.getAttribute("size")) ;
                id.putAttribute("size",size);
                Symbol D = new Symbol(production.getLeft()) ;
                symbolStack.push(D) ;

                break;
            }

            default:
                popSymbolStack(production);
                symbolStack.push(new Symbol(production.getLeft()));
                break;
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


    /**
     * 画语法分析树
     * @param production
     */
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


    /**
     *  语法分析 错误处理
     */
    // TODO 语义分析所做的一些修改使得原先错误处理代码不可用，此处为简单版本（仅打印错误之处），待完善
    private void parserErrorHandle(int i) {
        Token token = tokens.get(i);
        System.out.println("Error at line: " + token.getLine() +
                " [" + tokens.get(i).toString() + "]");
    }


    public String getCodeList() {
        return codeList.toString();
    }

    public String getSemanticErrorMessage() {
        return semanticErrorMessage.toString();
    }

    public String getSymbolTable() {
        return symbolTable.toString();
    }

    public String getQuaternions(){
        return quaternions ;
    }


    public static void main(String[] args) {
        new Parser("test/test.txt");
    }
}