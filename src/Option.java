public class Option
{
    private String answer;
    private double value;
    
    // Answer is the string for each answer ex: "c) White Stratus"
    // Value starts at 0.0 and is added to when an answer wins one of the analyzation methods
    public Option(String str, double n)
    {
       answer = str;
       value = n;
    }
    
    public void addValue(double n)
    {
        value = value + n;
    }
    
    public String getAnswer()
    {
        return answer;
    }
    
    public double getValue()
    {
        return value;
    }
    
    // Return answer and goto next line
    public String toString() {
        return answer + "<br>";
    }
}
