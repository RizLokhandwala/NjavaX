import java.io.*;

public class ConfigReader {
    public static void main(String[] args) throws Exception
    {
        File configFile = new File(
            "./src/NjavaX.conf");

        BufferedReader configReader
            = new BufferedReader(new FileReader(configFile));
        
        String st;

        while ((st = configReader.readLine()) != null)
            System.out.println(st);

        configReader.close();
    }
}