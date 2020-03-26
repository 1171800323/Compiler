package lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {
    private final Set<Edge> edges = new HashSet<>();
    private final MapRuler mapRuler = new MapRuler();
    private final Map<Integer, Tag> endStates = new HashMap<>();


    /**
     * 图结构的构造方法，在这里就读取每一行，包括对终结状态的处理
     * @param filename
     */
    public Graph(String filename) {
        String str;
        try (FileInputStream inputStream = new FileInputStream(filename);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains("endstate:")) {
                    str = str.substring(9);
                    String[] endstate = str.split(" ");
                    for (int i = 0;i<endstate.length ; i++){
                        String[] temp = endstate[i].split("#") ;
                        int a = Integer.parseInt(temp[0].substring(1)); // 截取，不要第一个字符，就是左括号
                        String b = temp[1].substring(0,temp[1].length()-1) ; // 截取，不要后面的右括号
                        System.out.println(b);
                        endStates.put(a,Tag.fromString(b)) ;
                    }
//                    System.out.println("nb");
                }
                // 这一行不是标识终结状态行，是普通的一行，就有起点终点边
                else {
                    String[] string = str.split("#");
                    int source = Integer.parseInt(string[0]);
                    int target = Integer.parseInt(string[1]);
                    Edge edge = new Edge(source, target, string[2]);
                    edges.add(edge);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(endStates);

    }


    /**
     *  就是move方法，给定一个状态和一个输入，返回他的下一个状态
     * @param source
     * @param ch
     * @return
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
                    System.out.println(string);
                    // 判断现在的这条边的跳转集合中，包不包括读进来的字符
                    sets = mapRuler.getSet(string);
                    if (sets.contains(ch)) {
                        target = edge.getTarget();
                        flag = true;
                    }
                }
                if (string.length() == 1) {
                    if (string.equals(ch)) {
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

    public Set<Edge> getEdges() {
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

//        System.out.println(graph.getTarget(1, 'a'));
//        String s = "other1";
//        System.out.println(s.contains("other"));
    }
}

class Edge {
    private final int source;
    private final int target;
    private final String weight;

    public Edge(int source, int target, String weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    public String getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", target=" + target +
                ", weight='" + weight + '\'' +
                '}';
    }
}