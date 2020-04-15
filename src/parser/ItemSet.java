package parser;

import java.util.*;

public class ItemSet {

    private final Set<Item> itemSet = new HashSet<>();
    private final int number;
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

    public void setGotoTable(String symbol, int next) {
        gotoTable.put(symbol, next);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("I" + number + ": \n");
        for (Item item : itemSet) {
            stringBuilder.append(item.toString());
        }
        for (Map.Entry<String, Integer> entry : gotoTable.entrySet()){
            stringBuilder.append("by '"+entry.getKey()+"' go to next: "+entry.getValue()+"\n");
        }
        return  stringBuilder.toString();
    }

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
