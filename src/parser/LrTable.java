package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LrTable {

    public static final String startSymbol = "Program";
    public static final String acceptSymbol = "acc";
    public static final String stackBottom = "$";
    public static final String emptySymbol = "ε";

    private final Set<Production> productionSet = new HashSet<>();
    private final Map<String, Set<Production>> productionMap = new HashMap<>();

    private final Set<String> terminals = new HashSet<>();
    private final Set<String> nonTerminals = new HashSet<>();
    private Map<String, Set<String>> firstSet = new HashMap<>();

    private final Set<ItemSet> itemSets = new HashSet<>();
    private List<String> tableHead = new ArrayList<>();
    private String[][] actionTable;
    private Map<Integer, Map<String, Integer>> gotoTable = new HashMap<>();

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
                nonTerminals.add(left);
            }

            for (Production production : productionSet) {
                List<String> rightList = production.getRight();
                for (String string : rightList) {
                    if (!nonTerminals.contains(string) && !string.equals(emptySymbol)) {
                        terminals.add(string);
                    }
                }
            }

            terminals.add(stackBottom);
            tableHead.addAll(terminals);
            tableHead.addAll(nonTerminals);
            tableHead.remove(startSymbol);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Production production : productionSet) {
            System.out.println(production.getLeft() + "->" + production.getRight());
        }
        getFirstSet();
    }

    public void getFirstSet() {
        for (String noTerminal : nonTerminals) {
            findFirst(noTerminal, productionMap.get(noTerminal));
        }
        for (Map.Entry<String, Set<String>> entry : firstSet.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue() + "");
        }
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

    public Set<String> getTerminals() {
        return terminals;
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<Production> getProduction(String left) {
        return productionMap.get(left);
    }

    public static void main(String[] args) {
        LrTable lrTable = new LrTable("src/parser/grammar.txt");
    }

}
