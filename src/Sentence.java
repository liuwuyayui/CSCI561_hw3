import java.util.*;

public class Sentence {
    Set<Literal> literals;      // literals in a sentence
    Map<String, Set<Literal>> nameToLiteralList;
    int sentenceHashCode;
    Sentence p1;
    Sentence p2;
    
    public Sentence() {
        literals = new HashSet<>();
    }
    
    public Sentence(String rawSentence) {
        if (rawSentence == null || rawSentence.length() == 0) {
            throw new IllegalArgumentException("raw sentence can not be null or empty");
        }
        
        literals = new HashSet<>();
        String[] splitSentence = rawSentence.split(" ");
        if (splitSentence.length == 1) {
            literals.add(new Literal(splitSentence[0]));
        } else {
            for (int i = 0; i < splitSentence.length; i += 2) {
                Literal newLiteral = new Literal(splitSentence[i]);
                if (i < splitSentence.length - 1) {
                    newLiteral.negate();
                }
                literals.add(newLiteral);
            }
        }
        indexingAndHash();
    }
    
    public Sentence(Literal literal) {
        literals = new HashSet<>();
        literals.add(literal);
        indexingAndHash();
    }
    
    public void indexingAndHash() {
        nameToLiteralList = new HashMap<>();
        for (Literal l : literals) {
            Set<Literal> literalList = nameToLiteralList.computeIfAbsent(l.name, k -> new HashSet<>());
            literalList.add(l);
        }
        sentenceHashCode = mySentenceHashcode();
    }
    
    public Set<String> getAllVars() {
        Set<String> set = new HashSet<>();
        for (Literal l : literals) {
            set.addAll(l.getVars());
        }
        return set;
    }
    
    public void replaceVar(String target, String str) {
        for (Literal l : literals) {
            l.replaceVarWithStr(target, str);
        }
    }
    
    public void removeLiteral(Literal literal) {
        literals.remove(literal);
        nameToLiteralList.get(literal.name).remove(literal);
    }
    
    public Sentence clone() {
        Sentence clone = new Sentence();
        clone.literals = new HashSet<>();
        for (Literal l : literals) {
            clone.literals.add(l.clone());
        }
        clone.indexingAndHash();
        return clone;
    }
    
    public void printWithParent() {
        p1.printSentence(true);
        System.out.print(" #  ");
        p2.printSentence(true);
        printSentence(true);
    }
    public String printSentence(boolean show) {
        StringBuilder sb = new StringBuilder();
        for (Literal literal : literals) {
            sb.append(literal.printLiteral(false));
            sb.append(" V ");
        }
        sb.delete(sb.length() - 3, sb.length());
        if (show) {
            System.out.println(sb.toString());
        }
        return sb.toString();
    }
    
    // according to the name of literals
    public int mySentenceHashcode() {
        int myHashCode = 1;
        for (Literal l : literals) {
            myHashCode += l.name.hashCode();
        }
        return myHashCode;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence = (Sentence) o;
        if (this.literals.size() != sentence.literals.size()) {
            return false;
        }
        int size = this.literals.size();
//        for (int i = 0; i < size; i++) {
            for (Literal l1 : this.literals) {
//            Literal l1 = this.literals.get(i);
            Set<Literal> corresInThat = sentence.nameToLiteralList.get(l1.name);
            if (corresInThat == null) {
                return false;
            }
            if (this.nameToLiteralList.get(l1.name).size() != corresInThat.size()) {
                return false;
            }
            boolean found = false;
            for (Literal litInThat : corresInThat) {
                if (areEquivalentLiterals(l1, litInThat)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
//        return Objects.hash(literals, nameToLiteralList, sentenceHashCode);
        return mySentenceHashcode();
    }
    
    public boolean areEquivalentLiterals(Literal l1, Literal l2) {
        if (!l1.name.equals(l2.name)) {
            return false;
        }
        for (int i = 0; i < l1.arguments.size(); i++) {
            String l1Arg = l1.arguments.get(i);
            String l2Arg = l2.arguments.get(i);
            if (isConst(l1Arg) && isConst(l2Arg) && !l1Arg.equals(l2Arg)) {
                return false;
            } else if (isVar(l1Arg) && isConst(l2Arg) || isVar(l2Arg) && isConst(l1Arg)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isConst(String str) {
        return Character.isUpperCase(str.charAt(0));
    }
    
    private boolean isVar(String str) {
        return Character.isLowerCase(str.charAt(0));
    }
    

    
    
    public static void main(String[] args) {
        Sentence s1 = new Sentence("Healthy(B) & Learn(A,x1) => Learn(y,x1)");
        Sentence s2 = new Sentence("Learn(A,x) & Healthy(B) => Learn(y,x)");
        boolean result = s1.equals(s2);
        System.out.println(result);

    }
}
