# Kaustav Vats (2016048)

# Multi Client Chatting App using TCP socket

# Usage:
- Server: ./server <port>
- Client: ./client <ip> <port>

# How to run these programs?
- In terminal type 'make -f Makefile_16048' to compile all C files.
- create n+1 terminals. n clients and 1 server.
- First start the server by entering './server <port>'
- To create a client, in each terminal enter './client <ip> <port>'
- In each client enter the name then, type a message to broadcast.
- Enter 'exit' to close a client or a server. Press CTRL+C to abruptly close a client or server.

# How to remove Object files of the program?
- Enter 'make -f Makefile_16048 clean' to remove client and server(object) files.

# Note:- 
- Maximum client my application can handle right now is 17.
- Client count can be easily increased by increasing the connection variable count and size of 'fds' and 'pthread ids'.
