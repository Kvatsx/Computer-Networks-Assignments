// Kaustav Vats (2016048)

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private volatile int AckCount;
    private volatile Packet[] data;
    private volatile boolean[] AckReceived;
    private int PacketCount;
    private int WindowSize;
    private int PORT;
    private static final int BUFFSIZE = 65535;

    public Client(String ipAddress, int portNumber, int winSize, int packetCount) throws SocketException, UnknownHostException, IOException{
        this.clientSocket = new DatagramSocket();
        this.AckCount = 0;
        this.PacketCount = packetCount;
        this.data = new Packet[this.PacketCount];
        this.AckReceived = new boolean[this.PacketCount]
        this.WindowSize = winSize;
        this.IPAddress = InetAddress.getByName(ipAddress);
        this.PORT = portNumber;

//        newConnectionHandler();
    }

//    public void newConnectionHandler() throws IOException {
//        byte[] buffer = Packet.getBytes(new Packet(0, "SYN"));
//        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, PORT);
//        System.out.println("Sending: SYN");
//        clientSocket.send(datagramPacket);
//
//        buffer = new byte[BUFFSIZE];
//        datagramPacket = new DatagramPacket(buffer, buffer.length);
//        clientSocket.receive(datagramPacket);
//        Packet p = Packet.getObject(datagramPacket.getData());
//        System.out.println("From Server: " + p.getData());
//
//        buffer = Packet.getBytes(new Packet(0, "ACK"));
//        datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, PORT);
//        System.out.println("Sending: ACK");
//        clientSocket.send(datagramPacket);
//
//        System.out.println("Connection established!\n");
//    }

//    private class SendMessageHandler implements Runnable {
//
//        public synchronized void run() {
//            try {
//                boolean DataSent = false;
//                while ( !DataSent ) {
//                    for ( int i=0; i<PacketCount; i++ ) {
//                        buffer = Packet.getBytes(new Packet(AckCount, "ACK"));
//                        datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, PORT);
//                        System.out.println("Sending: Ack "+AckCount);
//                        clientSocket.send(datagramPacket);
//
//                        if ( AckReceived[] )
//                    }
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private class ReceiverMessageHandler implements Runnable {

        public void run() {
            try {
                while(AckCount < PacketCount) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    clientSocket.receive(datagramPacket);
                    Packet p = Packet.getObject(datagramPacket.getData());
                    int high = AckCount + WindowSize;
                    if ( high > PacketCount ) {
                        high = PacketCount;
                    }

                    if ( p.getSeqNumber() >= high || p.getSeqNumber() < AckCount ) {
                        System.out.println("Rejecting Packet due to window size: "+p.getSeqNumber());
                        continue;
                    }
                    System.out.println("From Server: " + p.getData());
                    if ( p.getSeqNumber() == 2 ) {
                        System.out.println("Data Loss in Packet");
                        continue;
                    }
                    data[p.getSeqNumber()] = p;
                    for ( int i=AckCount; i<=PacketCount; i++ ) {
                        AckCount = i;
                        if ( i<PacketCount && data[i] == null ) {
                            break;
                        }
                    }
                    buffer = Packet.getBytes(new Packet(AckCount, "ACK"));
                    datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, PORT);
                    System.out.println("Sending: Ack "+AckCount);
                    clientSocket.send(datagramPacket);

                }
                System.out.println("Received all Packets!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String UserInput() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        return in.readLine();
    }

    public static void main(String args[]) throws IOException, SocketException {
        System.out.println("Welcome to Server-Client Chat Application...\n");
        if ( args.length != 3 ) {
            System.out.println("Usage: java Server <ip address> <Port> <Window Size> <Packet Count>");
            return;
        }
        Client client = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        new Thread(client.new ReceiverMessageHandler()).start();
    }
}


