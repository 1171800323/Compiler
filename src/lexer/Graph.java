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

    public Graph(String filename) {
        endStates.put(3, Tag.fromString("id"));
        String str;
        try (FileInputStream inputStream = new FileInputStream(filename);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains("endstate:")) {
                    str = str.substring(9);
                    String[] endstate = str.split(" ");
                    //TODO
                } else {
                    System.out.println(str);
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
    }

    public int getTarget(int source, char ch) {
        int target = -1;
        Set<Character> sets;
        Boolean flag = false;

        for (Edge edge : edges) {
            if (edge.getSource() == source) {
                String string = edge.getWeight();
                if (string.length() > 1 && !string.contains("other")) {
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
        Graph graph = new Graph("src/dfa.txt");
        for (Edge edge : graph.getEdges()) {
            System.out.println(edge.toString());
        }
        System.out.println(graph.getTarget(1, 'a'));
        String s = "other1";
        System.out.println(s.contains("other"));
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