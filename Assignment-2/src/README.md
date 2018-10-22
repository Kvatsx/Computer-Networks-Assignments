# Implement reliable data communication on top of this basic UDP communication.
- Receiver will receive all the packets and in the order that they have beens sent.
- Application include:- 
	- Timeouts
	- Acknowledgements
	- Retransmissions
	- Bufferring at receiver's end for in-order delivery
	- Receiver window to manage the flow
	- It must not be a "hold and wait" protocol i.e. only one message is being sent and next message is sent only after first message has been received correctly. The Window size should be "n" and taken as parameter in the beginning of running your program.

```
1) SimpleUDPServer_2016048.java & SimpleUDPClient_2016048.java are basic UDP implementation. To check number of correctly received packets at the receiver ends as well as cheching for number of packet lost in the network.
2) Server_2016048.java & Client_2016048.java include All above mentioned terms.

How to run these files?
1) Simply run 'Makefile' in your terminal. This will compile all above metioned files.
2) run java 'file name without .java' 'input for each file'

Note:- 
To check the input requirements.
Simply type java 'Filename without .java" then press Enter. This will show you the input requirements for that particular program.
```
