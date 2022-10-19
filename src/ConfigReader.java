import java.io.*;

public class ConfigReader {
    public static void main(String[] args) throws Exception
    {
        File configFile = new File(
            "./src/NjavaX.conf");

        BufferedReader configReader
            = new BufferedReader(new FileReader(configFile));
        
        String curLine;
        String trimmedLine;
        String[] lineArgs;

        while ((curLine = configReader.readLine()) != null) {
            trimmedLine = curLine.trim().replaceAll(" +", " ").replaceAll(" \\{", "\\{");
        }
            
        configReader.close();
    }
}