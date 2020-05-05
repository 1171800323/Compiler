package parser;

import java.util.HashMap;
import java.util.Map;

public class Symbol {
    private final String name;
    private final Map<String, String> attributes = new HashMap<>();

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
}
