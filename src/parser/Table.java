package parser;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final List<String> tableHead = new ArrayList<>();
    private final Action[][] actionTable;

    public Table(int statusNumber, List<String> tableHead) {
        tableHead.addAll(tableHead);
        this.actionTable = new Action[statusNumber][tableHead.size()];
    }

    public Action getAction(int status, String symbol){
        return actionTable[status][tableHead.indexOf(symbol)];
    }

    public void setTable(int status, String symbol, Action action){
        actionTable[status][tableHead.indexOf(symbol)] = action;
    }
}
