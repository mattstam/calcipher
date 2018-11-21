package stam.mainpackage;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Longest
{
     List<String> longs = new ArrayList<String>();

     // Looks for for longest answer per question
     public void findLongest(File file)
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
             sc.close();
         } 
         catch (Exception ex)
         {
             ex.printStackTrace();
         }
     
     }

}
