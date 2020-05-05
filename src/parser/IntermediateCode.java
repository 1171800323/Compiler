package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntermediateCode {
    private final List<String> code = new ArrayList<>();

    public IntermediateCode(String[] code) {
        this.code.addAll(Arrays.asList(code));
    }

    public void backPatch(String quad) {
        code.add(quad);
    }

    public List<String> getCode() {
        return code;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(code.get(0));
        for (int i = 1; i < code.size(); i++) {
            stringBuilder.append(" " + code.get(i));
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        IntermediateCode intermediateCode = new IntermediateCode(
                new String[]{"if", "a", "<", "b", "goto"}
        );
        intermediateCode.backPatch("11");
        System.out.println(intermediateCode.toString());
    }
}