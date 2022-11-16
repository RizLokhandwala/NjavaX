import java.io.*;
import java.util.*;

class ServerInfo{
    static int index;
    
     //public static void main(String[] args)
    //{
        ////avaiable instances
        //GlobalInfo instance = GlobalInfo.getInstance();
     
        //instance.addServerEntry("localhost", 8080);
        //instance.addServerEntry("127.12.12.1", 8080);
        //instance.addServerEntry("192.120.6.1", 8081);

        //ServerInfo.returnInfo(index);
        //index = changeIndex(index);
        //System.out.printf("Index changed to %s\n", index);
        
    //}

   

    public static List returnInfo(int a)
    {
        //Return IP and port info
        GlobalInfo instance = GlobalInfo.getInstance();
        List<Object> info = instance.getEntry(a);
         
        System.out.printf("Returned %s\n",info.get(0));
        System.out.printf("Returned %s\n",info.get(1));
        return info;

     }
     
     
     
     public static int changeIndex(int a)
     {
        //increments index or sets index to 0 if index is at last server entry
        GlobalInfo instance = GlobalInfo.getInstance();
        int num = instance.getNumServerEntries();
        num = num - 1;
        //System.out.printf("Printed %s\n",num);
      
        int index = a;
          
         if(a == num)
         {
            index = 0;
            System.out.printf("Returned %s\n",index);
            return index;
         }
         else
         {
            index += 1;
            System.out.printf("Returned %s\n",index);
            return index;
         }
     }

}

