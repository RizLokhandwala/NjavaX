import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class ConfigReader {
    final static String COMMENT = "##"; // anything after this in NjavaX.conf will be considered a comment and not be read
    static String configPath = "NjavaX.conf";
    public static GlobalInfo globalInfo;

    public static void main(String[] args) throws Exception
    {
        // If configFile is not found, create it.
        File configFile = new File(configPath);
        if (configFile.createNewFile()) {
            System.out.println("Could not find a configFile, File Created: \"./" + configFile.getName() + "\"");
        }
        else {
            System.out.println("configFile already exists. FILEPATH: \"./" + configFile.getName() + "\"");
        }
    }

    public ConfigReader(String confPath) {
        configPath = confPath;
    }

    public List<String> get(String param) throws Exception
    {
        List<String> returnArgs = new ArrayList<String>();

        File configFile = new File(
            configPath);
        
        BufferedReader configReader
            = new BufferedReader(new FileReader(configFile));

        String curLine;

        // loop through configFile, find the correct paramteter
        while((curLine = configReader.readLine()) != null) {

            // make sure line is in correct format
            String formattedLine
                = format(curLine);
            
            String[] args = formattedLine.split(" ", 2);
                // args[0] is the variable name
                // args[1] is the value

            if (args[0].equalsIgnoreCase(param)) { // parameter found
                
                // if a list of values
                if (args[1].equalsIgnoreCase("{")) { 
                    // loop through list
                    while((curLine = configReader.readLine()) != null) {
                        formattedLine
                            = format(curLine);
                        
                        // if empty line
                        if (formattedLine.equalsIgnoreCase("")) {continue;}

                        // if end of list of values
                        if (formattedLine.equalsIgnoreCase("}")) {break;} 

                        args = formattedLine.split(" ", 2);

                        // if a list within a list
                        if (args[1].equalsIgnoreCase("{")) {
                            // loop through inner list
                            while((curLine = configReader.readLine()) != null) {
                                formattedLine 
                                    = format(curLine);

                                // if empty line
                                if (formattedLine.equalsIgnoreCase("")) {continue;}
                                
                                // if end of inner list
                                if (formattedLine.equalsIgnoreCase("}")) {break;}

                                args = formattedLine.split(" ", 2);
                                returnArgs.add(args[0]+':'+args[1]);
                            }
                        } // end of inner list
                    }
                } // end of outer list
                else {returnArgs.add(args[1]);}
                configReader.close();
                return returnArgs;
            }
        }

        // parameter was not found in configFile, return "defualt"
        returnArgs.add("default");
        configReader.close();
        return returnArgs;
    }
    
    private static String format(String unformatted_line) // formats the line correctly and removes comments
    {
        String returnString;
        // remove comments
        returnString = unformatted_line.replaceAll(COMMENT+".*", "");

        // remove spaces from beginning and end >> ex:  (  var value  ) -> (var value)
        returnString = returnString.trim();

        // add a space before {
        returnString = returnString.replaceAll("\\{" , " \\{");

        // shrink remaining spaces down to one  >> ex:  (var     value) -> (var value)
        returnString = returnString.replaceAll(" +", " ");
        return returnString;
    }
}