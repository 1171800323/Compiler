package parser;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final List<String> tableHead = new ArrayList<>();
    private final Action[][] actionTable;

    public Table(int statusNumber, List<String> tableHead) {
        this.tableHead.addAll(tableHead);
        this.actionTable = new Action[statusNumber][tableHead.size()];
    }

    public Action getAction(int status, String symbol) {
        return actionTable[status][tableHead.indexOf(symbol)];
    }

    public void setTable(int status, String symbol, Action action) {
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
            stringBuilder.append("I"+number + "\n");
            for (int i = 0; i < strings.length; i++) {
                if (strings[i] != null) {
                    stringBuilder.append("(" + tableHead.get(i) + ", " + strings[i] + ")    ");
                }
            }
            stringBuilder.append("\n");
            number++;
        }
        return stringBuilder.toString();
    }
}
