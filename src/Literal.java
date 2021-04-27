import java.util.*;

public class Literal {
    String name;
    boolean isNegated;
    List<String> arguments;
    
    public Literal() {
    }
    
    public Literal(String rawLiteral) {
        List<String> literalSignature = parseLiteralSignature(rawLiteral);
        int pointer = 0;
        if (literalSignature.get(pointer).equals("~")) {
            isNegated = true;
            name = "~" + literalSignature.get(pointer + 1);
            ;
            pointer += 2;
        } else {
            name = literalSignature.get(pointer++);
        }
        arguments = literalSignature.subList(pointer, literalSignature.size());
    }
    
    // separate raw literal into ~, Predicate, args
    private List<String> parseLiteralSignature(String rawLiteral) {
        if (rawLiteral == null || rawLiteral.length() == 0) {
            throw new IllegalArgumentException("Literal can not be Null Or Empty");
        }
        List<String> result = new ArrayList<>();
        String[] tmp = rawLiteral.split("[\\(||\\)]");
        String[] args = tmp[1].split(",");
        if (tmp[0].charAt(0) == '~') {
            result.add("~");
            result.add(tmp[0].substring(1));
        } else {
            result.add(tmp[0]);
        }
        result.addAll(Arrays.asList(args));
        return result;
    }
    
    public Literal negate() {
        isNegated = !isNegated;
        name = name.charAt(0) == '~' ? name.substring(1) : "~" + name;
        return this;
    }
    
    public String negatedName() {
        return isNegated ? name.substring(1) : "~" + name;
    }
    
    public String printLiteral(boolean show) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        for (String arg : arguments) {
            sb.append(arg).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        if (show) {
            System.out.print(sb.toString());
        }
        return sb.toString();
    }
    
    public Set<String> getVars() {
        Set<String> set = new HashSet<>();
        for (String arg : arguments) {
            if (Character.isLowerCase(arg.charAt(0))) {
                set.add(arg);
            }
        }
        return set;
    }
    
    public void replaceVarWithStr(String target, String str) {
        for (int i = 0; i < arguments.size(); i++) {
            String arg = arguments.get(i);
            if (arg.equals(target)) {
                arguments.set(i, str);
            }
        }
    }
    
    public Literal clone() {
        Literal clone = new Literal();
        clone.name = this.name;
        clone.isNegated = this.isNegated;
        clone.arguments = new ArrayList<>(this.arguments);
        return clone;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // references are the same
        if (o == null || getClass() != o.getClass()) return false;     // o is null or type not the same
        Literal literal = (Literal) o;  // cast
        return isNegated == literal.isNegated && name.equals(literal.name) && this.arguments.equals(literal.arguments);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, isNegated, arguments);
    }
    
    public static void main(String[] args) {
        Literal literal1 = new Literal("Learn(WalkOutdoors,Ares)");
        Literal literal2 = new Literal("Learn(WalkOutdoors,Ares)");
        System.out.println(literal1.equals(literal2));
    }
    
}
