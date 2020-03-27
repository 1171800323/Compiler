package lexer;

public class Edge {
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
