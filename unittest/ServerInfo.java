import java.io.*;
import java.util.*;

class Test1{

     public static void main(String[] args){

        //avaiable instances
        GlobalInfo instance = GlobalInfo.getInstance();
        instance.addServerEntry("localhost", 8080);
        instance.addServerEntry("127.12.12.1", 8080);
        instance.addServerEntry("192.120.6.1", 8081);
        //int[] serverInfo = new int[]{2, 0};
        int index = 0;
        //Two two = new Two();
        //x = two.fxn(x);
        //List<Object> serverInfo = new ArraysList<Object>();
        //List<ServerEntry> entryList = new ArrayList<ServerEntry> (); 
        Test1.returnInfo(index);
        int x = changeIndex(index);
        //System.out.printf(x);
        //int[] x = changeIndex(serverInfo[0], serverInfo[1], serverInfo);
        //System.out.printf("Index changed to %s\n",x[0]);
        //System.out.printf("Index changed to %s\n",x[1]);
        
    }

    public static List returnInfo(int a)
    {
        //Get host and port info
        GlobalInfo instance = GlobalInfo.getInstance();

        List<Object> info = instance.getEntry(a);
        System.out.printf("Returned %s\n",info.get(0));
        System.out.printf("Returned %s\n",info.get(1));

        return info;

     }
     
     public static int changeIndex(int a)
     {
         //changes hostname and IP
        GlobalInfo instance = GlobalInfo.getInstance();
        int num = instance.getNumServerEntries();
        //System.out.printf("Returned %s\n",num);
        int index = a;
         if(a < num)
         {
            index += 1;
            System.out.printf("Returned %s\n",index);
            return index;
         }
         else
         {
            index = 0;
            return index;
         }
     }

    // public static int[] changeIndex(int a, int b, int[] info)
    // {
    //     //if not current server, increment index
    //     GlobalInfo instance = GlobalInfo.getInstance();
    //     int num = instance.getNumServerEntries();
    //     num = num * 2;
    //     System.out.printf("Returned %s\n",num);

    //     int[] serverInfo = new int[num];
    //     serverInfo[0] = a + 1;
    //     info[a] = serverInfo[0];
    //     serverInfo[1] = b + 1;
    //     info[b] = serverInfo[1];
    //     System.out.printf("Returned %s\n",serverInfo[0]);
    //     System.out.printf("Returned %s\n",serverInfo[1]);
    //     return serverInfo;
    // }
     
    //  public static List returnPn(int b)
    // {
    //     GlobalInfo instance = GlobalInfo.getInstance();
    //     instance.addServerEntry("localhost", 8080);
    //     instance.addServerEntry("127.12.12.1", 8080);
    //     instance.addServerEntry("192.120.6.1", 8081);
    //     //int num = instance.getNumServerEntries();
    //     List<Object> rvalue = instance.getEntry(b);
    //     System.out.printf("Returned %s\n",rvalue.get(b));
    //     return rvalue;
    //  }
}
