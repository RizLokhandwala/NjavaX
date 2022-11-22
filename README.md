# NjavaX
This project is developed by:

-Riz Lokhandwala
-Glenn Turner
-Sean Nguyen

We will be creating a java equivalent of NginX. To do this we will be working in 2 week sprints.

## Running the Program

The program may be run by:  
```NjavaX mode {0,1,2,3,4} portno {port number} config {config file name} landing {folder for landing page}```  
All of these are optional  
  
Defaults are:
- mode 0
- portno 8080 (this is listening port)
- config .\NjavaX.cnfg
- landing /tmp/webpagefiles

### The "Modes" of the program:
- Mode 0: The program is a simple web server -- a web page is served up from the landing path
- Mode 1: The program is a reverse proxy server -- the client is hidden from the server the client connects on "portno"  and another port is used to connect to a server.  Load balancing may be used.
- Mode 2: This is a simple web server that connects to the reverse proxy on the port number set in the instance running in mode 1.
- Mode 3: This is proxy server where multiple domains are mapped through this server to specific IP/ports 
- Mode 4: This is what we refer to as a direct proxy.  A port numnber is associated with a URL such that connection to a port will be forwarded to the desired URL
