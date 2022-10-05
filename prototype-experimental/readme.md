## This is a description of the applications in subfolders
### Client-ServerPair
 - this is two small programs that communicate through a socket
# WebServer
  This applicaion is a simple multithreading web server, same as Server.java.  
  It consists of two classes one to listen for a connection and the other to service each 
  connection on a thread. 
  This may be the seed to start NjavaX
  This program looks for html files and a jpg file in the folder <strong> /tmp/www/ </strong>
  <strong>This must be on the same drive you run the program, e.g. F:</strong>
  Copy needed files into that directory
  To build in <strong>prototype-experimental\Webserver</strong> folder:
  ```
  javac WebServerMain.java ClientHandler.java
  java WebServerMain
  ```
  Then in a browser address bar type
  ```
  http:\\localhost:8080
  ```

## This will describe the files in this folder
 - Server.java is a simple TCP connection to port 8080
The files used by this program are assumed to be in folder \tmp\www\.
This default location may be modifed in the function "getFilePath"

