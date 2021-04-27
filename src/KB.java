import java.util.*;

public class KB {
    // key: symbol(include ~)
    // value: set of sentence
//    Map<String, Set<Sentence>> map;
    List<Sentence> sentences;
    
    public KB() {
//        map = new HashMap<>();
        sentences = new ArrayList<>();
    }
    
    public void tellWithRawSentenceString(String rawSentenceString) {
        tellWithSentence(new Sentence(rawSentenceString));
    }
    
    public void tellWithSentence(Sentence sentence) {
        if (!sentences.contains(sentence)) {
            sentences.add(sentence);
        } else {
            if (Homework.SHOW) {
                System.out.println("sentence already exists in the KB");
            }
        }
//        for (Literal literal : sentence.literals) {
//            Set<Sentence> sentences = map.computeIfAbsent(literal.name, k -> new HashSet<>());
//            sentences.add(sentence);
//        }
    }
    
    public void tellWithQuery(Literal literal) {
        sentences.add(0, new Sentence(literal));
//        tellWithSentence(new Sentence(literal));
    }
    
    // replace variables with unique identities
    public void standardize() {
        int count = 1;
        for (Sentence sentence : sentences) {
            Set<String> vars = sentence.getAllVars();
            for (String var : vars) {
                sentence.replaceVar(var, "x" + count);
                count++;
            }
        }
    }
    
    public KB clone() {
        KB clone = new KB();
        clone.sentences = new ArrayList<>();
        for (Sentence s : sentences) {
            clone.sentences.add(s.clone());
        }
        return clone;
    }
    
    public void printKB() {
        for (Sentence s : sentences) {
            s.printSentence(Homework.SHOW);
        }
        System.out.println();
    }
    
    
    public static void main(String[] args) {
        KB kb = new KB();
        kb.tellWithRawSentenceString("Ready(A) & Ready(y) & Socialize(A,y) & Socialize(y,A) => Play(A,y)");
        kb.tellWithRawSentenceString("Ready(y) & Ready(A) & Socialize(A,y) & Socialize(y,A) => Play(A,y)");
        kb.printKB();
    }
}
