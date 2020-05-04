package parser;

import java.util.ArrayList;
import java.util.List;

public class Table {

    // 表头，包括终结符和非终结符，去掉增广文法开始符号和ε
    private final List<String> tableHead = new ArrayList<>();
    // 动作记录，可以是移入、规约、跳转、接收、错误处理
    private final Action[][] actionTable;

    // 根据状态数目、表头即可初始化
    public Table(int statusNumber, List<String> tableHead) {
        this.tableHead.addAll(tableHead);
        this.actionTable = new Action[statusNumber][tableHead.size()];
    }

    // 根据当前状态和输入符号确定应该执行的动作
    public Action getAction(int status, String symbol) {
        return actionTable[status][tableHead.indexOf(symbol)];
    }

    // 当前状态、输入符号以及动作
    public void setTable(int status, String symbol, Action action) {
//        System.out.println(symbol);
//        System.out.println(status);
        actionTable[status][tableHead.indexOf(symbol)] = action;
    }

    public List<String> getTableHead() {
        return tableHead;
    }

    public Action[][] getActionTable() {
        return actionTable;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        int number = 0;
        for (Action[] strings : actionTable) {
            stringBuilder.append("I").append(number).append("\n");
            for (int i = 0; i < strings.length; i++) {
                if (strings[i] != null) {
                    stringBuilder.append("(").append(tableHead.get(i)).append(", ").append(strings[i]).append(")    ");
                }
            }
            stringBuilder.append("\n");
            number++;
        }
        return stringBuilder.toString();
    }
}
