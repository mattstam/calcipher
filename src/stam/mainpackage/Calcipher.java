package stam.mainpackage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Calculates the best possible answer for multiple-choice questions using techniques to maximize accuracy
 * without any other outside resources or knowledge.
 */

public class Calcipher
{
    // Individual biases for each type of technique
    // If these biases sum to a greater value than BOLD_LIMIT, the answer is bolded in the final answerkey
    // Make adjustments to these values to change
    private static final double PATTERN_BIAS = 1;
    private static final double LENGTH_BIAS = 0.5;
    private static final double BOLD_LIMIT = 0.9;
    private static List<String> lines = new ArrayList<String>();
    private static Option[] choices;
    private static int[] answerSizes;
    
    public static void main(String[] args) 
	{
        System.out.print("Enter the test file: ");
        Scanner sc = new Scanner(System.in);
        File file = new File(sc.nextLine());
        
        // Generate List of questions from file
        List<String> questions = readFile(file);
        
        // Generate an empty Option[] with correct size and values of 0.0
        choices = generateChoices(lines, questions);
        
        // Looks for word repetitions in all of the answers
        // Also prioritizes "All of the above" type answers
        List<String> repeaters;
        repeaters = analyze(questions);
        
        // Looks for for longest answer
        List<String> longs;
        longs = findLongest(questions);
        
        Option[] answerKey;
        
        addPatterns(repeaters);
        addLongest(longs);
        
        answerKey = choices;

        createHTML(addNewLine(boldAnswers(answerKey), answerSizes));
        
        sc.close();
    }
    
    // Pattern answer winners get bias value added
    private static void addPatterns(List<String> repeaters)
    {
        for (int i = 0; i < repeaters.size(); i++)
        {
            for (int j = 0; j < lines.size(); j++)
            {
                if(repeaters.get(i).equals(lines.get(j))) 
                {
                    choices[j].addValue(PATTERN_BIAS);
                }
            }
        }
        
    }
    
    // Longest answer winners get bias value added
    private static void addLongest(List<String> longs)
    {
        for (int i = 0; i < longs.size(); i++)
        {
            for (int j = 0; j < lines.size(); j++)
            {
                if(longs.get(i).equals(lines.get(j))) 
                {
                    choices[j].addValue(LENGTH_BIAS);
                }
            }
        }
    }
    
    // Bold any answer that is greater than the BOLD_LIMIT set
    private static List<String> boldAnswers(Option[] answers)
    {
        List<String> outputLines = new ArrayList<String>();
        
        for (int i = 0; i < answers.length; i++)
        {
            if(answers[i].getValue() > BOLD_LIMIT)
            {
                outputLines.add("<b>" + answers[i].toString() + "</b>");
            }
            else
            {
                outputLines.add(answers[i].toString());
            }
            
        }
        return outputLines;
    }
    
    // Add return space in between each question's answers
    private static List<String> addNewLine(List<String> outputLines, int[] answerSizes)
    {   
        int size = answerSizes[0];
        for (int i = 0; i < answerSizes.length-1; i++)
        {
            for(int j = 0; j < outputLines.size()+i; j++)
            {
                if(j == size)
                {
                    outputLines.add(j+i, "<br>");
                }
            }
            size = size + answerSizes[i+1];
        }
        return outputLines;
    }
    
    // Write final output lines to html file
    public static void createHTML(List<String> outputLines) {
        try 
        {
            Charset utf8 = StandardCharsets.UTF_8;
            Files.write(Paths.get("AnswerKey.html"), outputLines, utf8);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    
    // Generate empty Option[] with correct size and values of 0.0
    public static Option[] generateChoices(List<String> lines, List<String> questions)
	{
        Option[] choices;
        String[] separator;
        
        // Put all answers into array
        for (int i = 0; i < questions.size(); i++)
        {
            separator = questions.get(i).split("\n");
            
            // Start at 1 to skip over question line to get only answer lines
            for (int j = 1; j < separator.length; j++)
            {
                lines.add(separator[j]);
            }
        }
        
        // Creates Options[] from those answers all with value 0.0
        Option[] temp = new Option[lines.size()];
        
        for (int i = 0; i < lines.size(); i++)
        {
            temp[i] = new Option(lines.get(i), 0.0);
        }
        
        choices = temp; 
        
        return choices;
    
    }
 
    public static List<String> readFile(File file)
    {
        List<String> questions = new ArrayList<>();
        
    	try 
        {
            Scanner sc = new Scanner(file);
            
            String delimiter = System.getProperty("line.separator");
            sc.useDelimiter(delimiter);
            StringBuilder sb = new StringBuilder();
            
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
        }
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}

    	return questions;
    }

    // Looks for words that are repeated in a answer bunch 
    // Selects answers with the highest number of these word repeaters
    // Also gives inherent word repeater activation toward "None of the above" type questions
    public static List<String> analyze(List<String> questions)
	{
        List<String> result = new ArrayList<String>();

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
        
        return result;
    }

    // Looks for for longest answer per question
    public static List<String> findLongest(List<String> questions)
    {
 
        List<String> longs = new ArrayList<String>();
    
        // Splits each question + answer combination into their own array
        // Than finds the answer with the maximum length and adds it to longs list
        String[] separator = new String[0];
        for (int i = 0; i < questions.size(); i++)
        {
            separator = questions.get(i).split("\n");
           
            int index = 0;
            int max = separator[1].length();
            
            // Start at 1 to skip over question line to get only answer lines
            for (int j = 1; j < separator.length; j++)
            {
                if(separator[j].length() > max)
                {
                    index = j;
                    max = separator[j].length();
                }
            }
            longs.add(separator[index]);
        }
        
        return longs;
    }
}
