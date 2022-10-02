# User Story Backlog
## User Stories for Proxy Server
### Pass a URL to another server
As a user I enter an URL into my browser that request a read from a server so I can see the web page.
(The system NjavaX passes this request to either ServerA, ServerB, or server)
### Determine Load Balance
As a user I enter an URL into my browser that request a read from a server so I can see the web page.
(The system NjavaX passes this request to either ServerA, ServerB, or server)
### Retrieve file from least busy server
As a user I enter the url for our webserver into the browser, it will get reverse proxied by NjavaX.
(NjavaX receives a request from the user, it will first determine how many users are currently active and on which servers. It will then direct the request to the server with the least load.
When the server has finished processing a request, it will send the results back to NjavaX.
NjavaX then receives results from a webserver, it will send the results back to the user.)
### Pass a URL to another server for File retrieval
As a user, I enter a URL into my browser to request to read from a server.
(The system (NjavaX) must reverse proxy this connection and determine the load on ServerA, ServerB, and ServerC. The System will then contact the server with the least load.)

## Stories for Web Server
### Fetch a Web page with file option on it
As a user I enter the URL for a web page on the NjavaX server so I may select options on it.
(The server produces the file)\
### Request a file but the file is not there
As a user I enter the URL for a web page on the NjavaX server so I may select options on it. The file is not there.
(A "File not found" needs to be displayed on the web page requesting the file)
### Fetch a web page through an encripted connection (https)
As a user I go to a site that uses HTTPS, NjavaX Decrypts and encrypts the data instead of the sites servers each individually doing so.

## Stories for File server
### Fetch a picture to a browser
As a user I enter the file name on a web page for a picture I want to see.
(The server determines the file exist and transfers it to the web page.)

