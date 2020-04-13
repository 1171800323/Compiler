package parser;

public class Item {
    private final String left;
    private final String[] right;
    private final int status;
    private final String lookahead;

    public Item(String left, String[] right, int status, String lookahead){
        this.left = left;
        this.right = right;
        this.status = status;
        this.lookahead = lookahead;
    }

    public String getLeft() {
        return left;
    }

    public int getStatus() {
        return status;
    }

    public String[] getRight() {
        return right;
    }

    public String getLookahead() {
        return lookahead;
    }
}
