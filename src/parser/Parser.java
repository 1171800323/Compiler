package parser;

import lexer.Lexer;
import lexer.Token;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;


public class Parser {

    private final Table table;
    public static final String shiftSymbol = "shift";
    public static final String reduceSymbol = "reduce";
    public static LrTable lrtable = new LrTable("src/parser/grammar.txt");
    public static List<Integer> linesnum = new ArrayList<>(); //保存每一个的行号
    private final List<String> errorMessages = new ArrayList<>();
    private Map<String ,Set<String>> followSetMap = new HashMap<>();


    private final ArrayList<DefaultMutableTreeNode> TreeList = new ArrayList<>();


    public Parser() {
//        LrTable table = new LrTable("src/parser/grammar_test.txt");
        table = lrtable.getLrTable() ;

        Lexer lexer = new Lexer("test/ex1.txt");


        linesnum = lexer.getLinesnum();  //这一步是获得对应的行号
        List<Token> tokens1 = lexer.getTokens() ;  //这一步是获得词法分析的tokens

        System.out.println(tokens1);
        System.out.println(linesnum);

        List<String> tokens2 = tokenChange(tokens1);  //获得解析后的tokens
        System.out.println(tokens2);
        System.out.println(linesnum);

        this.followSetMap = lrtable.getFollowSet() ;

        handle(tokens2); //执行规约过程

        for (String err : errorMessages){
            System.err.println(err);
        }

    }



    /**
     * 将词法分析获得的token，变成语法分析需要的形式token
     * @param tokens
     * @return
     */
    public List<String> tokenChange(List<Token> tokens){
        List<String> tokens2 = new ArrayList<>() ;
        for ( int i = 0;i<tokens.size();i++) {
            Token token = tokens.get(i);

            System.out.println(token.tag.toString());
            switch(token.tag.toString()){
                case "NOTE" :
                    linesnum.remove(i);
                    break ;
                case "AND" :
                    tokens2.add("&&");
                    break ;
                case "OR" :
                    tokens2.add("||");
                    break ;
                case "NOT" :
                    tokens2.add("!");
                    break ;
                case "EQ" :
                    tokens2.add("==");
                    break ;
                case "NE" :
                    tokens2.add("!=");
                    break ;
                case "LE" :
                    tokens2.add("<=");
                    break ;
                case "GE" :
                    tokens2.add(">=");
                    break ;
                case "GT" :
                    tokens2.add(">");
                    break ;
                case "LT" :
                    tokens2.add("<");
                    break ;
                case "ADD" :
                    tokens2.add("+");
                    break ;
                case "SUB" :
                    tokens2.add("-");
                    break ;
                case "MUL" :
                    tokens2.add("*");
                    break ;
                case "DIV" :
                    tokens2.add("/");
                    break ;
                case "REMAIN" :
                    tokens2.add("%");
                    break ;
                case "INC" :
                    tokens2.add("++");
                    break ;
                case "DEC" :
                    tokens2.add("--");
                    break ;
                case "ASSIGN" :
                    tokens2.add("=");
                    break ;
                case "SEMI" :
                    tokens2.add(";");
                    break ;
                case "COMMA" :
                    tokens2.add(",");
                    break ;
                case "DOT" :
                    tokens2.add(".");
                    break ;
                case "LS" :
                    tokens2.add("(");
                    break ;
                case "RS" :
                    tokens2.add(")");
                    break ;
                case "LM" :
                    tokens2.add("[");
                    break ;
                case "RM" :
                    tokens2.add("]");
                    break ;
                case "LB" :
                    tokens2.add("{");
                    break ;
                case "RB" :
                    tokens2.add("}");
                    break ;
                case "OCT" :
                    tokens2.add("num");
                    break ;
                case "HEX" :
                    tokens2.add("num");
                    break ;
                default:
                    tokens2.add(token.tag.toString().toLowerCase());
            }
        }
        tokens2.add("$");
        return tokens2 ;
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
            int errorStatus = i;

//            System.err.println(statusStack);
//            System.err.println(symbolStack);

            if(statusStack.empty()){
                return;
            }

            // 查ACTION表，根据当前状态栈顶符号和token确定动作
            Action action = table.getAction(statusStack.peek(), token);
            if (statusStack.size() != symbolStack.size()) {
                // 当状态栈和符号栈数目不同，需要查GOTO表
                action = table.getAction(statusStack.peek(), symbolStack.peek());
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


            //z这里开始错误处理
            else if(action == null){
                System.out.println(i);

                if(i == tokens.size()-1){
                    errorMessages.add("Error at Line ["+"末尾"+"]:  '"+token+"' ");
                    return;
                }

                //错误位置
                int line1 = linesnum.get(i-1);
                int line2 = linesnum.get(i);
                if(line1 == line2){
                    errorMessages.add("Error at Line ["+line1+"]:  '"+token+"' ");
                }
                else if(line1 < line2){
                    errorMessages.add("Error at Line [" + line1 +"~" +line2+"]:  "+"  "+token);
                }


                // 赋值语句缺少分号
                if(symbolStack.get(symbolStack.size()-2).equals("=") &&(symbolStack.peek().equals("id") || symbolStack.peek().equals("real") ||
                        symbolStack.peek().equals("num")||symbolStack.peek().equals("character")) && line2>line1 ){
                    tokens.add(i,";");
                    linesnum.add(i, line1);
                    i = i-1;
                    continue;
                }
                // 重复
                else if( symbolStack.peek().equals(token) && !token.equals("(")  && !token.equals(")") && !token.equals("{") && !token.equals("}")){
                    tokens.remove(i);
                    linesnum.remove(i);
                    i = i-  1;
                    continue;
                }
                // 缺少 if 后面左括号
                else if( symbolStack.peek().equals("if") && !token.equals("(") ){
                    tokens.add(i,"(");
                    linesnum.add(i, line1);
                    i = i-  1;
                    continue;
                }
                // 结构体缺少左大括号
                else if( symbolStack.peek().equals("id") && !token.equals("(")  && symbolStack.get(symbolStack.size()-2).equals("struct")){
                    tokens.add(i,"{");
                    linesnum.add(i, line1);
                    i = i-  1;
                    continue;
                }
                // 缺少右括号
                Action action1 = table.getAction(statusStack.peek(), ")");
                if( action1!= null  &&  action1.getAction().equals(reduceSymbol) ){
                    tokens.add(i,")");
                    linesnum.add(i, line1);
                    i = i-1;
                    continue;
                }
                // 缺少右大括号
                Action action2 = table.getAction(statusStack.peek(), "}");
                if( action2 != null  &&  action2.getAction().equals(reduceSymbol) ){
                    tokens.add(i,"}");
                    linesnum.add(i, line1);
                    i = i-1;
                    continue;
                }
                // 缺少右中括号
                Action action3 = table.getAction(statusStack.peek(), "]");
                if( action3 != null  &&  action3.getAction().equals(reduceSymbol) ){
                    tokens.add(i,"]");
                    linesnum.add(i, line1);
                    i = i-1;
                    continue;
                }

                //如果不满足常见的几种错误
                int errorState = statusStack.peek(); //当前状态栈顶，状态
                int state = errorState ;
                String errorSymbol = symbolStack.peek();  //当前符号栈符号
                Set<String> nonTerminals = lrtable.getNonTerminals(); //非终结符集合

                //如果是空，说明这个项集里面全是规约状态，接着往前找
                if(lrtable.getGraph().get(state)==null){
                    statusStack.pop();
                    symbolStack.pop();
                    state = statusStack.peek();//state永远指向栈顶
                }

                // 进行操作，看看是否含有非中介符。前一个set会保留交集，第二个set不变
                nonTerminals.retainAll( lrtable.getGraph().get(state).keySet() ) ;
                while ( nonTerminals.isEmpty() ){
                    //当GOTO表中没有这个可去的状态时，也就是终结符交集是空,接着往前找
                    statusStack.pop();
                    symbolStack.pop();

                    if(statusStack.empty()){
                        break;
                    }

                    state = statusStack.peek();
                    nonTerminals = lrtable.getNonTerminals();
                    nonTerminals.retainAll( lrtable.getGraph().get(state).keySet() ) ;
                }

                //到这里来，找到一个状态，他的非终结符集合交集不空了，
                String A  = "";
                int nextState = 0;
                Map<String ,Integer> map = lrtable.getGraph().get(state);
                for (String s : nonTerminals ){
                    A = s;
                    int j = i;
                    nextState = map.get(A) ;
                    boolean isflag = false;

                    while (j<i+3){
                        statusStack.push(nextState);
                        symbolStack.push(A);
                        if(j >= tokens.size()){
                            return;
                        }
                        if( followSetMap.get(A).contains(tokens.get(j)) ){
                            isflag = true;
                            break;
                        }
                        else {
                            statusStack.pop();
                            symbolStack.pop();
                            j++;
                        }
                    }
                    if(isflag == true){
                        i= i+(j-i)-1;
                        break;
                    }  else if(isflag == false){
                        //这个不符合条件，就把刚才加进去的再出去.然后开启下次循环
                        statusStack.pop();
                        symbolStack.pop();
                    }
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
