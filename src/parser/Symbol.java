package parser;

import java.util.*;

public class Symbol {
    private final String name;
    private final Map<String, String> attributes = new HashMap<>();
    private final Map<String, List<Integer>> listMap = new HashMap<>();

    public Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void putAttribute(String key, String val) {
        attributes.put(key, val);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public List<Integer> getList(String listType) {
        return new ArrayList<>(listMap.get(listType));
    }

    public void addList(String listType, List<Integer> list) {
        listMap.put(listType, new ArrayList<>(list));
    }

    public void mergeList(String listType, List<Integer> list1, List<Integer> list2) {
        Set<Integer> sets = new HashSet<>(list1);
        sets.addAll(list2);
        listMap.put(listType, new ArrayList<>(sets));
    }
}