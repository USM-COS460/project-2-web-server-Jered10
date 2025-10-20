## COS 460/540 - Computer Networks
# Project 2: HTTP Server

# Jered Kalombo

This project is written in Java on Eclipse.

## How to compile

I used Eclipse for this project. You don't have to manually compile anything. Eclipse automatically does that for me. Just make sure all the files (like HttpServer.java) are inside the project's src folder.

## How to run

#### - In Eclipse, right-click the project and go to Run As -> Run Configurations
#### - Under the Arguments tab, in the Program arguments box, type the port number and the folder for your document root.
#### eg. 1029 Folder
#### - click Apply, then Run
#### - Open a browser and go to: 
#### http://localhost:1029
#### If everything is correct you should see the HTTPD Server Test Page, and clicking the kitten image should open the larger image. 

## My experience with this project

I used Java sockets and threads to build a basic HTTP server that can handle multiple requests. It was interesting to see how browsers talk to servers through raw text requests and how sending proper headers makes or breaks everything. Setting up the file paths and MIME types took a bit of trial and error but once everything clicked it worked smoothly in Eclipse. 
