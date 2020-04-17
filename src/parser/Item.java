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
        // 处理空产生式的情况
        if (right.size() == 1 && right.contains(LrTable.emptySymbol)) {
            return true;
        } else {
            return location == right.size();
        }
    }

    public Production getProduction() {
        String[] right = new String[this.right.size()];
        this.right.toArray(right);
        return new Production(left, right);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>(right);
        list.add(location, "·");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left).append(" -> ");
        for (String string : list) {
            stringBuilder.append(string).append(" ");
        }
        stringBuilder.append(", ").append(lookahead).append("\n");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        // List比较是否相等是比较内容是否相同
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
