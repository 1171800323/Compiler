package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

 // 一条中间代码
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

//        return code.toString() ;
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

     /**
      *  构造一条中间代码对应的四元式序列 .
      * @return 返回四元式序列String形式
      */
    public String getOneQuaternion() {

        String[] quaternion = new String[4];
        for (int i = 0; i<quaternion.length; i++){
            quaternion[i] = "_";
        }
        //if a < b  goto 102
        if( code.contains("if") && code.contains("goto")){
            quaternion[0] = "j"+code.get(2) ;
            quaternion[1] = code.get(1) ;
            quaternion[2] = code.get(3) ;
            quaternion[3] = code.get(5) ;
        }
        //goto 100
        else if( !code.contains("if") && code.contains("goto") ){
            quaternion[0] = "j" ;
            if(code.size()>1){
                quaternion[3] = code.get(1) ;
            }
        }
        //x = y
        else if( code.contains("=") && code.size() == 3  && !code.get(2).contains("[") ){
            quaternion[0] = "=" ;
            quaternion[1] = code.get(2) ;
            quaternion[3] = code.get(0) ;
        }
        //x = y[i]
        else if( code.contains("=") && code.size() == 3  && code.get(2).contains("[")  ){
            String y = (code.get(2).split("\\["))[0] ;
            String i = (code.get(2).split("\\["))[1].split("]")[0] ;
            quaternion[0] = "=[]" ;
            quaternion[1] = y ;
            quaternion[2] = i ;
            quaternion[3] = code.get(0) ;
        }

        //t1 = x + 1
        else if( code.contains("=") && code.size() > 3 && ! code.contains("[") ){
            quaternion[0] = code.get(3) ;
            quaternion[1] = code.get(2) ;
            quaternion[2] = code.get(4) ;
            quaternion[3] = code.get(0) ;
        }
        //x[i] = y
        else if( code.contains("=") && code.size() > 3 && code.contains("[") ){
            quaternion[0] = "[]=" ;
            quaternion[1] = code.get(5) ;
            quaternion[2] = code.get(0) ;
            quaternion[3] = code.get(2) ;
        }
        //call getSum , 2
        else if( code.contains("call") ){
            quaternion[0] = "call" ;
            quaternion[1] = code.get(1) ;
            quaternion[2] = code.get(3) ;
        }
        //return sum
        else if( code.contains("return") ){
            quaternion[0] = "return" ;
            quaternion[1] = code.get(1) ;
        }
        //param a
        else if( code.contains("param") ){
            quaternion[0] = "param" ;
            quaternion[1] = code.get(1) ;
        }







        String result = "("+quaternion[0]+", "+quaternion[1]+", "+quaternion[2]+", "+quaternion[3]+")" ;
        return result ;
    }



}