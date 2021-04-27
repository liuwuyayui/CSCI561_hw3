import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Homework {
    public static final boolean SHOW = false;
    
    public static void main(String[] args) {
        List<String> input = readInput("input.txt");
    
        List<Literal> queries = new ArrayList<>();
        KB kb = new KB();
        parseQueryAndKB(input, queries, kb);    // parse input into Queries and KB
        kb.printKB();  // TODO: remove
        System.out.println();// TODO: remove
        kb.standardize();
        kb.printKB();// TODO: remove
        
        Resolution resolution = new Resolution(kb);

        StringBuilder sb = new StringBuilder();
        for (Literal query : queries) {
            boolean result = resolution.resolution_refutation(query);
            String resultInStr = result ? "TRUE" : "FALSE";
            sb.append(resultInStr);
            sb.append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        writeToFile(sb.toString(), "", "output.txt");
    }
    
    private static void parseQueryAndKB(List<String> input, List<Literal> queries, KB kb) {
        if (queries == null || kb == null) {
            throw new IllegalArgumentException("Queries and KB can not be null");
        }
        int numQueries = Integer.parseInt(input.get(0));
        int KBSize = Integer.parseInt(input.get(numQueries + 1));
        for (int i = 0; i < numQueries; i++) {
            queries.add(new Literal(input.get(i + 1)));
        }
        for (int i = 0 ; i < KBSize; i++) {
            kb.tellWithRawSentenceString(input.get(i + numQueries + 2));
        }
    }
    
    public static List<String> readInput(String inputFilePath) {
        List<String> input = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFilePath)))) {
            String newLine;
            while ((newLine = bufferedReader.readLine()) != null) {
                input.add(newLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }
    
    private static void writeToFile(String str, String destPath, String fileName) {
        try (FileWriter fileWriter = new FileWriter(destPath + fileName);
             BufferedWriter output = new BufferedWriter(fileWriter);
        ) {
            output.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


