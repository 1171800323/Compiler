
package lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.*;


public class Lexer {

    private final List<Row> lines = new ArrayList<>();
    private final List<Token> tokens = new ArrayList<>();
    private final Graph graph = new Graph("src/dfa.txt");

    public Lexer(String filename) {
        readFile(filename);
        findTokens();
    }

    /**
     * 最主要的生成token的函数，这里面对每一行的每一个字符进行识别判断，调用了move方法
     */
    private void findTokens() {
        int s = 1;
        int olds = 1;
        Map<Integer, Tag> endStates = graph.getEndStates();
        //对于每一行
        String temp = "";
        for (Row row : lines) {
            String line = row.getLine();
            int i = 0;
            while (i < line.length()) {
                char c = line.charAt(i);
                olds = s;
                s = graph.getTarget(olds, c);
                // 如果跳转之后在起始状态，说明读入的是纯空格，就不要他。
                if (s == 1) {
                    i++;
                    continue;
                }
                temp = temp + c; // 这就是设置一个缓存，在到达终结转态之前. 把读取的字符保留成一串
                // 如果下一个是终结状态
                if (endStates.keySet().contains(s)) {
                    int otherflag = 0;
                    List<Edge> edges = graph.getEdges();
                    // 如果是通过 other 然后到的终结状态，就应该保留这个字符到下一轮
                    for (Edge e : edges) {
                        if (e.getSource() == olds && e.getTarget() == s && e.getWeight().contains("other")) {
                            // 输出的时候把刚刚读进来的other字符去掉。
                            String symbol = temp.substring(0, temp.length() - 1);
                            System.out.println(symbol);
                            addToken(symbol, s);
                            temp = "";
                            otherflag = 1;
                            s = 1;        // 状态归一
                            i = i - 1;
                            break;
                        }
                    }
                    if (otherflag == 0) {
                        String symbol = temp;
                        System.out.println(symbol);
                        addToken(symbol, s);
                        s = 1;           // 状态归一
                        temp = "";
                    }
                }
                i++;
            }
        }
    }

    /**
     * 根据接收状态和接受字符串构建Token
     * @param symbol 接收单词
     * @param state 接收状态
     */
    private void addToken(String symbol, int state) {
        Tag tag = graph.getEndStates().get(state);
        switch (tag) {
            case ID:
                if (graph.isKeyWord(symbol)){
                    tokens.add(new Token(Tag.fromString(symbol)));
                    return;
                }
            case NOTE:
            case CHARACTER:
                tokens.add(new Word(symbol, tag));
                break;
            case NUM:
            case OCT:
            case HEX:
                tokens.add(new Num(parseToNum(symbol), tag));
                break;
            case REAL:
                tokens.add(new Real(parseToReal(symbol)));
                break;
            default:
                tokens.add(new Token(tag));
                break;
        }
    }

    /**
     * 将字符串常数解析成int
     * @param symbol 字符串类型的整数、八进制数和十六进制数
     * @return 字符串的值
     */
    private int parseToNum(String symbol) {
        int result = 0, n = 0;
        // 十六进制
        if(symbol.contains("x") || symbol.contains("X")){
            // 注意消去0x，所以从 i = 2 开始
            for (int i = 2;i<symbol.length();i++){
                char c = symbol.charAt(i);
                if(c >= 'A' && c <= 'F')//十六进制还要判断字符是不是在A-F或者a-f之间
                    n = c-'A'+10;
                else if(c >= 'a'&& c <='f')
                    n = c-'a'+10;
                else
                    n= c -'0';
                result = result*16 + n;
            }
        }
        // 八进制
        else if(symbol.length() > 1 && symbol.charAt(0) == '0'){
            for (int i = 1 ; i<symbol.length() ; i++){
                char c = symbol.charAt(i);
                n= c -'0';
                result = result * 8 + n;
            }
        }
        // 十进制
        else {
            for (int i = 0 ; i<symbol.length() ; i++){
                char c = symbol.charAt(i);
                n= c -'0';
                result = result * 10 + n;
            }
        }

        return result;
    }



    /**
     * 将字符串常数解析成float
     * @param symbol 字符串类型的浮点数、科学计数法
     * @return 字符串的值
     */
    private float parseToReal(String symbol){
        float result = 0;

        return result;
    }



    /**
     * 读取测试用例的方法
     *
     * @param filename 文件地址+文件名
     */
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