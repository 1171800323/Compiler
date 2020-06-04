package main;


public class Main {
    public static void main(String[] args) {
        String type = "int";
        int width = 1;
        switch (type) {
            case "int":
                width =  4;
                break;
            case "float":
                width =  8;
                break;
            case "char":
                width =  1;
                break;
            default:
                for (String s : type.split(",\\s*")) {
                    int temp = s.indexOf("(");
                    if (temp != -1) {
                        int tempWidth = Integer.parseInt(s.substring(temp + 1));
                        width *= tempWidth;
                    } else {
                        String elemType = s.substring(0, s.indexOf(")"));
                        switch (elemType) {
                            case "int":
                                width *= 4;
                                break;
                            case "float":
                                width *= 8;
                                break;
                            case "char":
                                width *= 1;
                                break;
                        }
                    }
                }
        }
        System.out.println(width);
    }
}
