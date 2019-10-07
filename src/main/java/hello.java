import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class hello {

    public static boolean perfectString(String s) {
        if (s.equals("")) return true;

        char[] chars = s.toCharArray();
        if (chars.length % 2 == 1) return false;

        HashMap<Character, Character> m = new HashMap<>();
        m.put(')','(');
        m.put(']','[');
        m.put('}','{');

        Stack<Character> a = new Stack<>();
        for (char ch : chars) {
            if (m.containsValue(ch)) {
                a.push(ch);
            } else if (a.empty() || m.get(ch) != a.pop()) {
                return false;
            }
        }
        return a.empty();
    }

    public static int nibolan(String s) {
        if (s.equals("")) return 0;
        Set<Character> op = new HashSet<Character>(){{
            add('+');
            add('-');
            add('*');
            add('/');
        }};
        Stack<Character> c = new Stack<>();
        Stack<Integer> n = new Stack<>();

        char[] chars = s.toCharArray();

        for (int i=chars.length-1; i>=0; i--) {
            if (op.contains(chars[i]))
                c.push(chars[i]);
            else
                n.push((int) chars[i]);
        }
        return 1;
    }

    public static void main(String[] args) {
        System.out.println(perfectString(")({})"));
        System.out.println(perfectString("({})"));
        System.out.println(perfectString("([){}]"));
        System.out.println(perfectString("[(){}]"));
        System.out.println(perfectString("[({}){(}]"));
        System.out.println(perfectString("[(()){[]}]"));
    }
}
