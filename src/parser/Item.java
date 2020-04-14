package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Item {
    private final String left;         // 产生式左部
    private final List<String> right = new ArrayList<>();  // 产生式右部
    private final int location;        // ·的位置
    private final String lookahead;    // 展望符
    // P -> · D P, $
    // left为P，right是list，分别为D和P，location为0，展望符为$
    // P -> D · P, $，location为1

    public Item(String left, List<String> right, int location, String lookahead) {
        this.left = left;
        this.right.addAll(right);
        this.location = location;
        this.lookahead = lookahead;
        assert location > right.size();
    }

    public String getLeft() {
        return left;
    }

    public int getLocation() {
        return location;
    }

    public List<String> getRight() {
        return new ArrayList<>(right);
    }

    public String getLookahead() {
        return lookahead;
    }

    public Boolean isReduceItem() {
        return location == right.size();
    }

    @Override
    public String toString() {
        right.add(location, "·");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left + " -> ");
        for (String string : right) {
            stringBuilder.append(string + " ");
        }
        stringBuilder.append(", " + lookahead + "\n");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return location == item.location &&
                Objects.equals(left, item.left) &&
                Objects.equals(right, item.right) &&
                Objects.equals(lookahead, item.lookahead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, location, lookahead);
    }
}
