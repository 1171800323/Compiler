
package lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            while (i<line.length()){
                char c = line.charAt(i);
                olds = s;
                s = graph.getTarget(olds, c);
//                System.out.println(s);

                // 如果跳转之后在起始状态，说明读入的是纯空格，就不要他。
                if(s == 1){
                    i++;
                    continue;
                }


                temp = temp + c;
                // 如果下一个是终结状态
                if (endStates.keySet().contains(s)) {
                    int otherflag = 0;
                    Set<Edge> edges = graph.getEdges();
                    // 如果是通过 other 然后到的终结状态，就应该保留这个字符到下一轮
                    for (Edge e : edges) {
                        if (e.getSource() == olds && e.getTarget() == s && e.getWeight().contains("other")) {
                            // 输出的时候把刚刚读进来的other字符去掉。
                            String symbol = temp.substring(0,temp.length()-1);
                            System.out.println(symbol);
//                            tokens.add(new Token())
                            temp = "";
                            otherflag = 1 ;
                            s = 1;
                            i = i- 1;
                            break ;
                        }
                    }
                    if(otherflag == 0){
                        String symbol = temp ;
                        System.out.println(symbol);
                        s = 1;
                        temp = "" ;
                    }
                }
                i++;
            }



//            for (int i = 0; i < string.length(); i++) {
//                char c = string.charAt(i);
//                s = graph.getTarget(s, c);
//                System.out.println(s);
//                //判断到没到终结状态
//                Map<Integer, Tag> endStates = graph.getEndStates();
//                if (endStates.keySet().contains(s)){
////                    tokens.add(new Word("INT",endStates.get(s)));
//                    s = 1;
//                }
//                }








        }
    }

    /**
     * 读取测试用例的方法
     * @param filename
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

    /**
     * 大main
     * @param args
     */
    public static void main(String[] args) {



        System.out.println("cguvhbijnkl;");
        Lexer lexer = new Lexer("test/ex1.txt");
        for (Token token : lexer.getTokens()) {
//            System.out.println("gtvyhbuj");
//            System.out.println(token.toString());
        }
    }
}