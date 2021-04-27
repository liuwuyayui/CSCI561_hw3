import java.util.*;

public class Resolution {
    
    KB kb;
    
    public Resolution() {
    }
    
    public Resolution(KB kb) {
        this.kb = kb;
    }
    
    public boolean resolution_refutation(Literal query) {
        KB cleanKB = kb.clone();
        Literal negQuery = query.negate();
        cleanKB.tellWithQuery(negQuery);
        cleanKB.printKB();
        System.out.println();
        
        while (true) {
            Set<Sentence> newlyGen = new HashSet<>();
            for (int i = 0; i < cleanKB.sentences.size(); i++) {
                for (int j = i + 1; j < cleanKB.sentences.size(); j++) {
                    Sentence s1 = cleanKB.sentences.get(i);
                    Sentence s2 = cleanKB.sentences.get(j);
                    List<Sentence> resolvents = resolve(s1, s2);
                    setParents(resolvents, s1, s2);
                    if (resolvents == null) {
                        return true;
                    }
                    printListOfSentence(resolvents, i + 1, j + 1, Homework.SHOW);
                    newlyGen.addAll(resolvents);
                }
            }
            printSetOfSentences(newlyGen);
            if (kbContainsAllResolvents(cleanKB, newlyGen)) {
                return false;
            }
            for (Sentence s : newlyGen) {
                cleanKB.tellWithSentence(s);
            }
            cleanKB.standardize();
            cleanKB.printKB();
        }
    }
    
    
    public void setParents(List<Sentence> resolvents, Sentence s1, Sentence s2) {
        if (resolvents == null) {
            return;
        }
        for (Sentence s : resolvents) {
            s.p1 = s1;
            s.p2 = s2;
        }
    }
    
    private boolean kbContainsAllResolvents(KB cleanKb, Set<Sentence> newlyGen) {
        for (Sentence sentence : newlyGen) {
            if (!cleanKb.sentences.contains(sentence)) {
                return false;
            }
        }
        return true;
    }
    
    private void printListOfSentence(List<Sentence> list, int i, int j, boolean show) {
        if (!show) {
            return;
        }
        if (list == null || list.isEmpty()) {
            return;
        }
        
        System.out.println("----------Resolvent of " + i + " and " + j);
        for (Sentence s : list) {
            s.printWithParent();
        }
        System.out.println();
    }
    
    private void printSetOfSentences(Set<Sentence> set) {
        if (set == null || set.isEmpty()) {
            return;
        }
        if (Homework.SHOW) {
            System.out.println("----------Newly Generated----------");
        }
        for (Sentence s : set) {
            s.printSentence(Homework.SHOW);
        }
        System.out.println();
    }
    
    /**
     * @param s1
     * @param s2
     * @return List<Sentences> as resolvents
     * null if resolvent contains empty result
     */
    public List<Sentence> resolve(Sentence s1, Sentence s2) {
        List<Sentence> result = new ArrayList<>();
        Set<Literal> litsInS1 = s1.literals;
        for (Literal s1Lit : litsInS1) {
            String negNameOfS1Lit = s1Lit.negatedName();
            Set<Literal> cplmLitsInS2 = s2.nameToLiteralList.get(negNameOfS1Lit);  // find all complementary literals in s2
            if (cplmLitsInS2 == null) {
                continue;
            }
            for (Literal cplmLit : cplmLitsInS2) {
                Map<String, String> unifier = unify(s1Lit, cplmLit);
                if (unifier == null) {
                    continue;
                } else {
                    Sentence resolvent = getResolvent(s1, s2, s1Lit, cplmLit, unifier);
                    if (resolvent.literals.size() == 0) {  // empty result
                        return null;
                    }
                    if (containsComplementary(resolvent)) {
                        continue;
                    }
                    result.add(resolvent);
                }
            }
        }
        return result;
    }
    
    public boolean containsComplementary(Sentence s) {
        for (Literal l : s.literals) {
            Literal negLit = l.clone().negate();
            Set<Literal> complementaryLiterals = s.nameToLiteralList.get(negLit.name);
            if (complementaryLiterals == null) {
                continue;
            }
            for (Literal cplLit : complementaryLiterals) {
                if (negLit.equals(cplLit)) {
                    return true;
                }
            }
        }
        return false;
    }

//    public boolean containsSamePredicate(Sentence s) {
//        for (Map.Entry<String, List<Literal>> entry : s.nameToLiteralList.entrySet()) {
//
//        }
//    }
    
    // pre-requisite:  l1, l2 are negated to each other
    // return null if cannot unify
    public Map<String, String> unify(Literal l1, Literal l2) {
        if (!l1.negatedName().equals(l2.name)) {
            throw new IllegalArgumentException("l1, l2 are not negated to each other");
        }
        
        Map<String, String> unifier = new HashMap<>();
        List<String> l1Args = l1.arguments;
        List<String> l2Args = l2.arguments;
        for (int i = 0; i < l1Args.size(); i++) {
            String l1Arg = l1Args.get(i);
            String l2Arg = l2Args.get(i);
            if (isConst(l1Arg) && isConst(l2Arg) && !l1Arg.equals(l2Arg)) {
                return null;
            } else if (isVar(l1Arg) && isConst(l2Arg)) {
                String prev = unifier.get(l1Arg);
                if (prev == null || isVar(prev)) {
                    unifier.put(l1Arg, l2Arg);
                } else if (!prev.equals(l2Arg)) {
                    return null;
                }
            } else if (isVar(l2Arg) && isConst(l1Arg)) {
                String prev = unifier.get(l2Arg);
                if (prev == null || isVar(prev)) {
                    unifier.put(l2Arg, l1Arg);
                } else if (!prev.equals(l1Arg)) {
                    return null;
                }
            } else if (isVar(l1Arg) && isVar(l2Arg)) {
                unifier.putIfAbsent(l1Arg, l2Arg);
            }
        }
        return unifier;
    }
    
    /**
     * @param s1      original sentence s1's reference
     * @param s2      original sentence s2's reference
     * @param l1
     * @param l2
     * @param unifier
     * @return
     */
    public Sentence getResolvent(Sentence s1, Sentence s2, Literal l1, Literal l2, Map<String, String> unifier) {
        Sentence s1Clone = s1.clone();
        Sentence s2Clone = s2.clone();
        s1Clone.removeLiteral(l1);
        s2Clone.removeLiteral(l2);
        for (Map.Entry<String, String> entry : unifier.entrySet()) {
            s1Clone.replaceVar(entry.getKey(), entry.getValue());
            s2Clone.replaceVar(entry.getKey(), entry.getValue());
        }
//        s1Clone.printSentence(true);
//        s2Clone.printSentence(true);
        return cat2Sentences(s1Clone, s2Clone);
    }
    
    /**
     * given 2 sentences are cloned
     *
     * @param s1
     * @param s2
     * @return
     */
    private Sentence cat2Sentences(Sentence s1, Sentence s2) {
        Sentence sentence = new Sentence();
        sentence.literals.addAll(s1.literals);
        sentence.literals.addAll(s2.literals);
////        s1.literals.addAll(s2.literals);
//        for (Literal l : s2.literals)  {
//            s1.literals.add(l);
//        }
        sentence.indexingAndHash();
        return sentence;
    }
    
    private boolean isConst(String str) {
        return Character.isUpperCase(str.charAt(0));
    }
    
    private boolean isVar(String str) {
        return Character.isLowerCase(str.charAt(0));
    }
    
    public static void main(String[] args) {
        Sentence s1 = new Sentence("Ready(x4) & Train(x5,x4) => Learn(x5,x4)");
        Sentence s2 = new Sentence("~Train(Drop,x66) & Ready(x66) => ~Train(Get,x66)");
        Resolution r = new Resolution();
        List<Sentence> resolvents = r.resolve(s1, s2);
        System.out.println();
        
    }
    
}
