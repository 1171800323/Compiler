package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LrTable {
    // 特殊的一些文法符号
    public static final String startSymbol = "Program";
    public static final String startSymbolReal = "P";
    //    public static final String startSymbol = "S'";
//    public static final String startSymbolReal = "S";
    public static final String acceptSymbol = "acc";
    public static final String stackBottom = "$";
    public static final String emptySymbol = "ε";

    // 产生式集合
    private final Set<Production> productionSet = new HashSet<>();
    private final Map<String, Set<Production>> productionMap = new HashMap<>();

    // 终结符或非终结符集合
    private final Set<String> terminals = new HashSet<>();
    private final Set<String> nonTerminals = new HashSet<>();

    // 非终结符的first集
    private final Map<String, Set<String>> firstSet = new HashMap<>();

    // 项集族及之间状态转移关系
    private final Set<ItemSet> itemSets = new HashSet<>();
    private final Map<Integer, Map<String, Integer>> graph = new HashMap<>();

    // LR(1)分析表
    private final Table lrTable;

    public LrTable(String filename) {
        try (FileInputStream inputStream = new FileInputStream(filename);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                String[] production = str.split("->");
                String left = production[0].trim();
                String right = production[1];
                String[] rightList = right.split("丨");
                for (String string : rightList) {
                    Production product = new Production(left, string.trim().split(" "));
                    productionSet.add(product);
                    productionMap.putIfAbsent(left, new HashSet<Production>());
                    productionMap.get(left).add(product);
                }
                // 非终结符
                nonTerminals.add(left);

            }
            for (Production production : productionSet) {
                List<String> rightList = production.getRight();
                for (String string : rightList) {
                    // 终结符除去非终结符和ε
                    if (!nonTerminals.contains(string) && !string.equals(emptySymbol)) {
                        terminals.add(string);
                    }
                }
            }
            terminals.add(stackBottom);

        } catch (IOException e) {
            e.printStackTrace();
        }
//        for (Production production : productionSet) {
//            System.out.println(production);
//        }

        // 计算各个非终结符的first集
        getFirstSet();

        // 建立了项集族以及各项集之间转移关系
        items();

        // 构造LR(1)分析表
        List<String> head = new ArrayList<>(terminals);
        head.addAll(nonTerminals);
        head.remove(startSymbol);
        lrTable = new Table(itemSets.size() + 2, head);  // 传入表头，状态数两个参数以初始化
        constructLrTable();
    }

    public Table getLrTable() {
        return lrTable;
    }

    private void constructLrTable() {
        System.out.println("----------------------");

        // 此处调试bug，未解决
//        Set<Integer> set = new HashSet<>();
//        List<Integer> list = new ArrayList<>();
//        for (ItemSet itemSet : itemSets) {
//            set.add(itemSet.getNumber());
//            list.add(itemSet.getNumber());
//        }
//        System.out.println(itemSets.size());
//        System.out.println(set.size());
//        System.out.println(set);
//        System.out.println(list.size());
//        list.sort(null);
//        System.out.println(list);
//        System.out.println("少: ");
//        for (int i = 0; i <= 201; i++) {
//            if (!set.contains(i)) {
//                System.out.println(i);
//            }
//        }

        /**/
        for (Map.Entry<Integer, Map<String, Integer>> entry : graph.entrySet()) {
            for (Map.Entry<String, Integer> entry1 : entry.getValue().entrySet()) {
                if (nonTerminals.contains(entry1.getKey())) {
                    Action action = new Action.Builder().status(entry1.getValue()).build();
                    lrTable.setTable(entry.getKey(), entry1.getKey(), action);
                } else {
                    Action action = new Action.Builder().action("shift").status(entry1.getValue()).build();
                    lrTable.setTable(entry.getKey(), entry1.getKey(), action);
                }
            }
        }
        for (ItemSet itemSet : itemSets) {
            if (itemSet.isAccept()) {
                lrTable.setTable(itemSet.getNumber(), stackBottom, new Action.Builder().action(acceptSymbol).build());
            } else if (itemSet.hasReduceItem()) {
                Set<Item> reduceItems = itemSet.getReduceItem();
                for (Item item : reduceItems) {
                    if (item.getLookahead().equals(emptySymbol)) {
                        System.out.println(item);
                    }
                    lrTable.setTable(itemSet.getNumber(), item.getLookahead(),
                            new Action.Builder().action("reduce").production(item.getProduction()).build());
                }
            }
        }
        System.out.println("LR(1) Table: ");
        System.out.println(lrTable.toString());


        System.out.println("----------------------");
    }

    private void getFirstSet() {
        for (String noTerminal : nonTerminals) {
            findFirst(noTerminal, productionMap.get(noTerminal));
        }
//        System.out.println("firstSet: ");
//        for (Map.Entry<String, Set<String>> entry : firstSet.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue() + "");
//        }
    }

    private Set<String> findFirst(String leftNode, Set<Production> rightNodes) {
        if (firstSet.containsKey(leftNode)) {
            return firstSet.get(leftNode);
        }
        Set<String> first = new HashSet<>();
        // P -> D P 丨 S P 丨 ε，遍历有相同左部的所有产生式，计算该左部的first集
        for (Production rightNode : rightNodes) {
            // 对P -> D P，依次计算D、P的first集
            for (String node : rightNode.getRight()) {
                // 如果D是终结符或者ε，则P的first集构造完成
                if (!nonTerminals.contains(node)) {
                    first.add(node);
                    break;
                } else {
                    // 如果是左递归E -> E + G，或者P -> D P中D可以推出ε，之后又要计算first(P)，
                    // 则终止计算，防止无限递归使得栈溢出
                    if (node.equals(leftNode)){
                        break;
                    }
                    // 如果D是非终结符，则计算D的first集，添加到P的first集中
                    Set<String> tempFirst = findFirst(node, productionMap.get(node));
                    first.addAll(tempFirst);
                    // 如果first(D)有ε，即D可以推出ε，还要继续循环，计算P的first集
                    if (!tempFirst.contains(emptySymbol)) {
                        break;
                    }
                }
            }
        }
        firstSet.put(leftNode, first);
        return first;
    }

    private Set<String> getFirstSetFromList(List<String> nodes) {
        Set<String> first = new HashSet<>();
        for (String node : nodes) {
            // 如果是非终结符，查询它的first集
            if (nonTerminals.contains(node)) {
                Set<String> tempSet = firstSet.get(node);
                first.addAll(tempSet);
                first.remove(emptySymbol);
                // 如果第一个node可以推出空，则要计算第二个node，否则直接终止
                if (!tempSet.contains(emptySymbol)) {
                    break;
                }
            } else if (terminals.contains(node)) { // 终结符，直接返回
                first.add(node);
                break;
            }
        }
        return first;
    }

    private Set<Item> getClosure(Set<Item> startItem) {
        Set<Item> items = new HashSet<>(startItem);
        while (true) {
            int itemsSize = items.size();
            Set<Item> tempItems = new HashSet<>();     // 设置此临时变量，防止遍历items时添加元素，造成错误
            Iterator<Item> itemIterator = items.iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                List<String> right = item.getRight();
                int location = item.getLocation();
                // 如果相等，则这是一个规约项目，否则，如A -> α · B β , a，还需将B -> · γ, b加入items
                if (!item.isReduceItem()) {
                    String B = right.get(location);
                    // 对每一个左部为B的产生式，如果B不是非终结符，则不用添加
                    if (nonTerminals.contains(B)) {
                        for (Production production : productionMap.get(B)) {
                            List<String> betaA = new ArrayList<>(right.subList(location + 1, right.size()));
                            betaA.add(item.getLookahead());
                            // 添加first(βa)中每个元素为展望符
                            for (String b : getFirstSetFromList(betaA)) {
                                tempItems.add(new Item(B, production.getRight(), 0, b));
                            }
                        }
                    }
                }
            }
            items.addAll(tempItems);
            // 终止条件，items不再增加元素
            if (items.size() == itemsSize) {
                break;
            }
        }
        return items;
    }

    private Set<Item> gotoFunction(Set<Item> items, String x) {
        Set<Item> sets = new HashSet<>();
        for (Item item : items) {
            // 如果不是规约项目
            if (!item.isReduceItem()) {
                // 如果这个项目，等待出现的符号是X
                // 如，A → α∙Xβ，a，要将A → αX∙β，a加入
                if (item.getRight().get(item.getLocation()).equals(x)) {
                    sets.add(new Item(item.getLeft(), item.getRight(), item.getLocation() + 1, item.getLookahead()));
                }
            }
        }
        return getClosure(sets);
    }

    private void items() {
        Set<Item> start = new HashSet<>();
        start.add(new Item(startSymbol, new ArrayList<>(Arrays.asList(startSymbolReal)), 0, stackBottom));
        int number = 0;
        // 添加I0项集族
        itemSets.add(new ItemSet(getClosure(start), number));
        number = 1;
        // 收集所有文法符号
        Set<String> symbols = new HashSet<>();
        symbols.addAll(nonTerminals);
        symbols.addAll(terminals);
        while (true) {
            int size = itemSets.size();
            Set<ItemSet> tempItemSet = new HashSet<>();
            Iterator<ItemSet> iterator = itemSets.iterator();
            // 对每个项集I
            while (iterator.hasNext()) {
                ItemSet itemSet = iterator.next();
                // 对每个文法符号X
                for (String symbol : symbols) {
                    Set<Item> closure = gotoFunction(itemSet.getItemSet(), symbol);
                    // 如果gotoFunction(I,X)非空
                    if (closure.size() > 0) {
                        ItemSet temp = new ItemSet(closure, number);
                        if (!itemSets.contains(temp)) {
                            // 设置项集族I经过X到达的下一个状态
                            itemSet.setGotoTable(symbol, number);
                            graph.putIfAbsent(itemSet.getNumber(), new HashMap<>());
                            graph.get(itemSet.getNumber()).put(symbol, number);
                            // 将gotoFunction(I,X)添加到itemSets中
                            tempItemSet.add(temp);
                            number += 1;
                        } else {
                            for (ItemSet itemSet1 : itemSets) {
                                if (itemSet1.equals(temp)) {
                                    // 设置项集族I经过X到达的下一个状态
                                    itemSet.setGotoTable(symbol, itemSet1.getNumber());
                                    graph.putIfAbsent(itemSet.getNumber(), new HashMap<>());
                                    graph.get(itemSet.getNumber()).put(symbol, itemSet1.getNumber());
                                }
                            }
                        }

                    }
                }
            }

            itemSets.addAll(tempItemSet);
            if (itemSets.size() == size) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        LrTable lrTable = new LrTable("src/parser/grammar_test1.txt");
//        LrTable lrTable = new LrTable("src/parser/grammar.txt");
//        System.out.println("ItemSets: ");
//        for (ItemSet itemSet : lrTable.itemSets) {
//            System.out.println(itemSet.toString());
//        }
//        System.out.println("size: " + lrTable.itemSets.size());
//        System.out.println(lrTable.graph);
    }
}
