import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculates the best possible answer for multiple-choice questions using techniques to maximize accuracy
 * without any other outside resources or knowledge.
 * 
 * Created by Matt-Stam on 12/29/17
 */

public class Calcipher
{
    // Individual biases for each type of technique
    // If these biases sum to a greater value than BOLD_LIMIT, the answer is bolded in the final answerkey
    // Make adjustments to these values to change
    private static final double PATTERN_BIAS = 1;
    private static final double LENGTH_BIAS = 0.5;
    private static final double BOLD_LIMIT = 0.9;
    
    public static void main(String[] args) 
	{
        System.out.print("Enter the test file: ");
        Scanner sc = new Scanner(System.in);
        File file = new File(sc.nextLine());
        
        // Generate empty Option[] with correct size and values of 0.0
        Estimate e = new Estimate();
        e.generateChoices(file);
        
        // Looks for word repetitions in all of the answers
        // Also prioritizes "All of the above" type answers
        Pattern p = new Pattern();
        p.analyze(file);
        
        // Looks for for longest answer
        Longest l = new Longest();
        l.findLongest(file);
        
        Option[] answerKey = new Option[0];
        
        addPatterns(p, e);
        addLongest(l ,e);
        answerKey = e.choices;

        createHTML(addNewLine(boldAnswers(answerKey), p.answerSizes));
        
        sc.close();
    }
    
    // Pattern answer winners get bias value added
    private static Estimate addPatterns(Pattern p, Estimate e)
    {
        for (int i = 0; i < p.result.size(); i++)
        {
            for (int j = 0; j < e.lines.size(); j++)
            {
                if(p.result.get(i).equals(e.lines.get(j))) 
                {
                    e.choices[j].addValue(PATTERN_BIAS);
                }
            }
        }
        
        return e;
    }
    
    // Longest answer winners get bias value added
    private static Estimate addLongest(Longest l, Estimate e)
    {
        for (int i = 0; i < l.longs.size(); i++)
        {
            for (int j = 0; j < e.lines.size(); j++)
            {
                if(l.longs.get(i).equals(e.lines.get(j))) 
                {
                    e.choices[j].addValue(LENGTH_BIAS);
                }
            }
        }
        return e;
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
        for (int i = 0; i < answerSizes.length; i++)
        {
            System.out.println(size);
            for(int j = 0; j < outputLines.size()+i-1; j++)
            {
                if(j == size)
                {
                    outputLines.add(j+i-1, "<br>");
                }
            }
            size = size + answerSizes[i];
        }
        return outputLines;
    }
    
    // Write final output lines to html file
    public static void createHTML(List<String> outputLines) {
        try {
            Charset utf8 = StandardCharsets.UTF_8;
            Files.write(Paths.get("AnswerKey.html"), outputLines, utf8);
            }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
 
}
