package parser;

import java.util.ArrayList;
import java.util.List;

public class CodeList {
    private final List<IntermediateCode> codeList = new ArrayList<>();
    private List<String> quaternions = new ArrayList<>();
    private int quad = 0;

    public CodeList() {

    }



    // 返回所有三地址指令的四元式
    public List<String> getQuaternions(){
        for (int i = 0; i < codeList.size(); i++) {
            quaternions.add(i+": "+codeList.get(i).getOneQuaternion()) ;
        }
        return quaternions ;
    }


    public void addCode(String[] code) {
        IntermediateCode intermediateCode = new IntermediateCode(code);
        codeList.add(intermediateCode);
        quad += 1;
    }

    public int getQuad() {
        return quad;
    }

    public void backPatch(List<Integer> list, String quad) {
        for (int num : list) {
            codeList.get(num).backPatch(quad);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < codeList.size(); i++) {
            stringBuilder.append(i + ": ").append(codeList.get(i)).append("\n");
        }
        return stringBuilder.toString().trim();
    }
}
