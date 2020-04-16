package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        if (right.size() == 1 && right.contains(LrTable.emptySymbol)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left + " -> ");
        for (String string : right) {
            stringBuilder.append(string + " ");
        }
        return stringBuilder.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
