package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {

    private final Map<String, SymbolItem> symbolItemMap = new HashMap<>();
    private final List<String> idList = new ArrayList<>();

    public SymbolTable() {
    }

    public Boolean isIdExisted(String id) {
        return symbolItemMap.containsKey(id);
    }

    public void put(String id, SymbolItem symbolItem) {
        symbolItemMap.put(id, symbolItem);
        idList.add(id);
    }
    public String getType(String id){
        return symbolItemMap.get(id).getType();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id-type-offset-lineNum\n");
        for (String id : idList) {
            stringBuilder.append(symbolItemMap.get(id).toString() + "\n");
        }
        return stringBuilder.toString().trim();
    }
}
