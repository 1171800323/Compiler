package parser;

import java.util.*;

public class ItemSet {
    private final Set<Item> itemSet = new HashSet<>();
    private final int number;
    private final Set<Item> reduceItem = new HashSet<>();
    private final Map<String, Integer> gotoTable = new HashMap<>();

    public ItemSet(Set<Item> itemSet, int number) {
        this.itemSet.addAll(itemSet);
        this.number = number;
    }

    public Set<Item> getItemSet() {
        return new HashSet<>(itemSet);
    }

    public int getNumber() {
        return number;
    }

    // 该项集是否有规约项目
    public Boolean hasReduceItem() {
        boolean flag = false;
        for (Item item : itemSet) {
            if (item.isReduceItem()) {
                flag = true;
                reduceItem.add(item);
            }
        }
        return flag;
    }

    public Boolean isAccept() {
        List<String> right = new ArrayList<>();
        right.add(LrTable.startSymbolReal);
        Item item = new Item(LrTable.startSymbol, right, 1, LrTable.stackBottom);
        return itemSet.size() == 1 && itemSet.contains(item);
    }

    public Set<Item> getReduceItem() {
        return reduceItem;
    }

    public void setGotoTable(String symbol, int next) {
        gotoTable.put(symbol, next);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("I").append(number).append(": \n");
        for (Item item : itemSet) {
            stringBuilder.append(item.toString());
        }
        for (Map.Entry<String, Integer> entry : gotoTable.entrySet()){
            stringBuilder.append("by '").append(entry.getKey()).append("' go to I").append(entry.getValue()).append("\n");
        }
        return  stringBuilder.toString();
    }

    // 仅关心itemSet这个集合内容是否相同
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSet itemSet1 = (ItemSet) o;
        if (itemSet1.getItemSet().size() != itemSet.size()){
            return false;
        }
        return itemSet.containsAll(itemSet1.getItemSet());
    }

    @Override
    public int hashCode() {
        // 由于关注的是itemSet中内容是否完全相同
        // 可以借助Arrays.hashCode根据指定数组的内容返回哈希码
        Item[] items = new Item[itemSet.size()];
        itemSet.toArray(items);
        return Arrays.hashCode(items);
    }
}
