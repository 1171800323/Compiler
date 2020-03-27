package lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Graph {
    private final List<Edge> edges = new ArrayList<>();
    private final MapRuler mapRuler = new MapRuler();
    private final Map<Integer, Tag> endStates = new HashMap<>();
    private final Set<String> keyWords = new HashSet<>();

    /**
     * 图结构的构造方法，在这里就读取每一行，包括对终结状态的处理
     * @param filename 文件地址+文件名
     */
    public Graph(String filename) {
        String str;
        try (FileInputStream inputStream = new FileInputStream(filename);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((str = bufferedReader.readLine()) != null) {
                if(str.contains("keyword:")){
                    String[] key = str.substring(6).split(" ");
                    keyWords.addAll(Arrays.asList(key));
                }else if (str.contains("endstate:")) {
                    str = str.substring(9);
                    String[] endstate = str.split(" ");

                    for (int i = 0;i<endstate.length ; i++){
                        String[] temp = endstate[i].split("#") ;
                        int a = Integer.parseInt(temp[0].substring(1)); // 截取，不要第一个字符，就是左括号
                        String b = temp[1].substring(0,temp[1].length()-1) ; // 截取，不要后面的右括号
//                        System.out.println(b);
                        endStates.put(a,Tag.fromString(b)) ;
                    }
                }
                // 这一行不是标识终结状态行，是普通的一行，就有起点终点边
                else {
                    String[] string = str.split("#");
                    int source = Integer.parseInt(string[0]);
                    int target = Integer.parseInt(string[1]);
                    String weight = string[2];
                    Edge edge = new Edge(source, target, weight);
                    edges.add(edge);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(endStates);

    }

    /**
     * 检测该接受字符串是否为关键字
     * @param symbol 接收的单词
     * @return true,则是关键字，否则是ID
     */
    public Boolean isKeyWord(String symbol){
        return keyWords.contains(symbol);
    }

    /**
     *  就是move方法，给定一个状态和一个输入，返回他的下一个状态
     * @param source 当前状态
     * @param ch 输入字符
     * @return 跳转到的下一个状态
     */
    public int getTarget(int source, char ch) {
        int target = -1;
        Set<Character> sets  ;
        Boolean flag = false;

        for (Edge edge : edges) {
            if (edge.getSource() == source) {
                String string = edge.getWeight();
                // 判断这条边上面的 跳转条件string，决定他要跳转到哪个终点上去。
                // 先检查那些不是other的边，包不包含。
                if (string.length() > 1 && !string.contains("other")) {
//                    System.out.println(string);
                    // 判断现在的这条边的跳转集合中，包不包括读进来的字符
                    sets = mapRuler.getSet(string);
                    if (sets.contains(ch)) {
                        target = edge.getTarget();
                        flag = true;
                    }
                }
                if (string.length() == 1) {
                    // 需要把ch转成string，再比较.
                    if (string.equals(ch+"")) {
                        target = edge.getTarget();
                        flag = true;
                    }
                }
            }
        }
        if (!flag) {
            for (Edge edge : edges) {
                if (edge.getSource() == source) {
                    String string = edge.getWeight();
                    if (string.contains("other")) {
                        target = edge.getTarget();
                    }
                }
            }
        }
        return target;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<Integer, Tag> getEndStates() {
        return endStates;
    }

    public static void main(String[] args) {
        // 先读取DFA转换表，存储到图结构中
        Graph graph = new Graph("src/dfa.txt");
        for (Edge edge : graph.getEdges()) {
//            System.out.println(edge.toString());
        }
    }
}

