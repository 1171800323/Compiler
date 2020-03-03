package lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static void main(String[] args) {
        String line = "xasxsa/**abc\"*/\",,,,,\"xsxas\",...\",.,.**/xsaxsax..*/";
        System.out.println(line);
//        String pattern = "/\\**([^\"*/]|\"\\*/\")*\\**/";
        String pattern = "/\\**([^\"*/]|\".*\"|\")*\\**/";
        System.out.println(pattern);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(line);
        System.out.println();
        System.out.println();
        System.out.println();
        if (m.find()){
            System.out.println(m.group(0));
        }else{
            System.out.println("not find");
        }
    }
}
