import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;

public class Pattern 
{
    List<String> result = new ArrayList<String>();
    int[] answerSizes = new int[0];

    // Looks for words that are repeated in a answer bunch 
    // Selects answers with the highest number of these word repeaters
    // Also gives inherent word repeater activation toward "None of the above" type questions
    public void analyze(File file)
	{
        try 
        {
            Scanner sc = new Scanner(file);
            
            // Split questions into list
            // Each String in list represents one question with its answers
            String delimiter = System.getProperty("line.separator");
            sc.useDelimiter(delimiter);
            StringBuilder sb = new StringBuilder();
            List<String> questions = new ArrayList<>();
            
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                if(!(line.trim().length() == 0))
                {
                    sb.append(line).append(delimiter);
                }
                else if
                (sb.toString().length() > 0) 
                {
                    questions.add(sb.toString());
                    sb.setLength(0);
                }
            }
            
            if(sb.toString().length() > 0) 
            {
                questions.add(sb.toString());
            }

            int[] questionLengths = new int[questions.size()];
            
            // Holds separated lines from each question
            String[] separator = new String[0];
            
            // Counts number of word repetitions
            for (int i = 0; i < questions.size(); i++)
            {
                List<String> lines = new ArrayList<String>();
                separator = questions.get(i).split("\n");
                
                // Start at 1 to skip over question line to get only answer lines
                for (int j = 1; j < separator.length; j++)
                {
                    lines.add(separator[j]);
                }
                
                questionLengths[i] = lines.size();  
                answerSizes = questionLengths;
                
                HashMap<String, HashSet<String>> data = new HashMap<>();
                HashMap<String, Integer> wordIndex = new HashMap<>();
                
                // Look at each word and add value of 1 if is repeated
                for (String line : lines)
                {
                    List<String> words = Arrays.asList(line.split(" "));
                    data.put(line, new HashSet<>(words));
                    for (String word : words)
                    {
                        wordIndex.merge(word, 1, Integer::sum);
                    }
                    
                    // Bias towards "All of the above" type answers
                    wordIndex.merge("All", 1, Integer::sum);
                    wordIndex.merge("all", 1, Integer::sum);
                    wordIndex.merge("Always", 1, Integer::sum);
                    wordIndex.merge("always", 1, Integer::sum);
                    wordIndex.merge("Never", 1, Integer::sum);
                    wordIndex.merge("never", 1, Integer::sum);
                    wordIndex.merge("None", 1, Integer::sum);
                    wordIndex.merge("none", 1, Integer::sum);
                }
                
                wordIndex.entrySet().removeIf(e->e.getValue() <= 1);
                
                for (Map.Entry<String, HashSet<String>> value : data.entrySet())
                {
                    value.getValue().retainAll(wordIndex.keySet());
                }
                    
                // Add key with highest max value, which is the answer with the most repeater words
                result.add(data.entrySet().stream().max((a, b) -> Integer.compare(a.getValue().size(), b.getValue().size())).get().getKey());       
            }
            sc.close();
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    
    }
 
}
