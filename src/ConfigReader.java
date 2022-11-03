import java.io.*;
//import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;

public class ConfigReader {
    public static GlobalInfo globalInfo;

    public static void main(String[] args) throws Exception
    {
        List<String> testInfo = get("http");
        for(Integer i = 0; i<testInfo.size(); i++) {
            System.out.println(testInfo.get(i));
        }

        List<String> user = get("user");
        System.out.println(user.get(0));
    }

    public static List<String> get(String param) throws Exception
    {
        List<String> returnArgs = new ArrayList<String>();

        File configFile = new File(
            "./NjavaX.conf");
        
        BufferedReader configReader
            = new BufferedReader(new FileReader(configFile));

        String curLine;
        while((curLine = configReader.readLine()) != null) {
            String trimmedLine
                = trimmer(curLine);
            
            String[] args = trimmedLine.split(" ", 2);
            if (args[0].equals(param)) {
                if (args[1].equals("{")) {
                    String newCurLine;
                    while(!((newCurLine = configReader.readLine()).equals("}"))) {
                        String newTrimmedLine
                            = trimmer(newCurLine);

                        String[] newArgs = newTrimmedLine.split(" ", 2);
                        returnArgs.add(newArgs[0]+':'+newArgs[1]);
                    }
                }
                else {returnArgs.add(args[0]+':'+args[1]);}

                configReader.close();
                return returnArgs;
            }
        }

        returnArgs.add("default");
        configReader.close();
        return returnArgs;
    }
    
    private static String trimmer(String untrimmed_line)
    {
        // remove spaces from beginning and end >> ex:  (  var value  ) -> (var value) 
        // shrink remaining spaces down to one  >> ex:  (var     value) -> (var value)

        String trimmedLine
            = untrimmed_line.trim().replaceAll( "\\{" , " \\{").replaceAll(" +", " ");
        return trimmedLine;
    }

    // regex instantiation
    /*
    private static String class_regex = "\\{";
    private static String param_regex = " ";
    private static Pattern class_pattern = Pattern.compile(class_regex);
    private static Pattern param_pattern = Pattern.compile(param_regex);
    
    private static String parser(String trimmed_line)
    {
        if (trimmed_line.isEmpty()) {
            return "empty";
        }

        Matcher class_matcher = class_pattern.matcher(trimmed_line);
        Matcher param_matcher = param_pattern.matcher(trimmed_line);

        if (class_matcher.find()) {
            return "class";

        }
        else if (param_matcher.find()) {
            return "param";
        }
        else {
            return "invalid";
        }

    }
    */
}