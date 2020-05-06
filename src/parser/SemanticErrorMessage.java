package parser;

import java.util.ArrayList;
import java.util.List;

public class SemanticErrorMessage {
    private final List<String> errorMessages = new ArrayList<>();

    public SemanticErrorMessage() {
    }

    public void add(int lineNum, String reason) {
        errorMessages.add("Error at line[" + lineNum + "]: " + reason);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : errorMessages) {
            stringBuilder.append(s + "\n");
        }
        return stringBuilder.toString().trim();
    }
}
