import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Estimate
{
    List<String> lines = new ArrayList<String>();
    Option[] choices = new Option[0];
    
    // Generate empty Option[] with correct size and values of 0.0
    public void generateChoices(File file)
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

            
            String[] separator = new String[0];
            
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
            
            sc.close();
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    
    }
 
}
