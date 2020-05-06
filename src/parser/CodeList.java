package parser;

import java.util.ArrayList;
import java.util.List;

public class CodeList {
    private final List<IntermediateCode> codeList = new ArrayList<>();
    private int quad = 0;

    public CodeList() {

    }

    public void addCode(String code) {
        String[] codeArray = code.split(" ");
        IntermediateCode intermediateCode = new IntermediateCode(codeArray);
        codeList.add(intermediateCode);
        quad += 1;
    }

    public int getQuad() {
        return quad;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (IntermediateCode intermediateCode : codeList){
            stringBuilder.append(intermediateCode.toString()).append("\n");
        }
        return stringBuilder.toString().trim();
    }
}
