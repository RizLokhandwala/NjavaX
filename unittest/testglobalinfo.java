import java.io.*;
import java.util.*;

// Test for global info singleton
class TestGlobalInfo {

    public static void main(String[] args)  {

        GlobalInfo instance = GlobalInfo.getInstance();
        int mode = instance.getMode();
        System.out.printf(" Mode = %d\n",mode);
        instance.setMode(2);
        System.out.printf(" After set Mode = %d\n",instance.getMode());

        instance.addServerEntry("localhost", 8080);
        instance.addServerEntry("127.12.12.1", 8080);
        instance.addServerEntry("192.120.6.1", 8081);
        int num = instance.getNumServerEntries();
        System.out.printf(" number of entries is %d\n",num);
        System.out.println(" now check instance");

        CheckInstance();

    }
    public static void CheckInstance()
    {
        GlobalInfo instance = GlobalInfo.getInstance();
        int num = instance.getNumServerEntries();
        System.out.printf(" number of entries is %d\n",num);
        List<Object> rvalue = instance.getEntry(1);
        System.out.printf("Returned %s\n",rvalue.get(0));

        // test range for mode
        instance.setMode(3);
        int mode = instance.getMode();
        System.out.printf(" Mode = %d\n",mode);

    }
}