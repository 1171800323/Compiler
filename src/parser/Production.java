package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Production {
    private final String left;
    private final List<String> right = new ArrayList<>();

    public Production(String left, String[] right) {
        this.left = left;
        this.right.addAll(Arrays.asList(right));
    }

    public String getLeft() {
        return left;
    }

    public List<String> getRight() {
        return right;
    }

    public Boolean isEmptyProduction() {
        return right.size() == 1 && right.contains(LrTable.emptySymbol);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left).append(" -> ");
        for (String string : right) {
            stringBuilder.append(string).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}
