package lexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapRuler {
    private final Map<String, Set<Character>> map = new HashMap<>();

    public MapRuler() {
        // "letter_" -- a-zA-Z_
        Set<Character> weight1 = new HashSet<>();
        weight1.add('_');
        for (int i = 65; i <= 90; i++) {
            weight1.add((char) i);
        }
        for (int i = 97; i <= 122; i++) {
            weight1.add((char) i);
        }
        map.put("letter_", weight1);


        // "digit" -- 0-9
        Set<Character> weight2 = new HashSet<>();
        for (int i = 0; i <= 9; i++) {
            String s = i+"";
            char c = s.charAt(0) ;
            weight2.add(c);
        }
        map.put("digit", weight2);


        // 1-9
        Set<Character> weight3 = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            String s = i+"";
            char c = s.charAt(0) ;
            weight3.add(c);
        }
        map.put("1-9", weight3);


        // 0-7
        Set<Character> weight4 = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            String s = i+"";
            char c = s.charAt(0) ;
            weight4.add(c);
        }
        map.put("0-7", weight4);


        // 0-9 or a-f or A-F
        Set<Character> weight5 = new HashSet<>();
        for (int i = 0; i <= 9; i++) {
            String s = i+"";
            char c = s.charAt(0) ;
            weight5.add(c);
        }
        for (int i = 97; i <= 102; i++) {
            weight5.add((char) i);
        }
        for (int i = 65; i <= 70; i++) {
            weight5.add((char) i);
        }
        map.put("0-9 or a-f or A-F", weight5);
    }

    public Set<Character> getSet(String string) {
        return map.get(string);
    }
}
