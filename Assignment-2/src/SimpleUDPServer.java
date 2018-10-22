// Kaustav Vats (2016048)

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

import static java.lang.Thread.sleep;

public class SimpleUDPServer {
    private DatagramSocket ServerSocket;
    private int PacketCount;
    private Packet[] data;
    private boolean[] AckBuffer;
    private boolean DataSend;

    // Client
    private InetAddress ipClient;
    private int portClient;

    private static final int BUFFSIZE = 65535;

    public SimpleUDPServer(int Port, int packetCount) throws SocketException, IOException {
        this.ServerSocket = new DatagramSocket(Port);
        this.PacketCount = packetCount;
        this.data = new Packet[packetCount];
        this.AckBuffer = new boolean[packetCount];
        this.DataSend = false;

        newConnectionHandler();
    }

    public void newConnectionHandler() throws IOException {
        byte[] buffer = new byte[BUFFSIZE];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        ServerSocket.receive(datagramPacket);
        Packet p = Packet.getObject(datagramPacket.getData());
        System.out.println("From Client: "+p.getData());

        this.ipClient = datagramPacket.getAddress();
        this.portClient = datagramPacket.getPort();
        buffer = Packet.getBytes(new Packet(0, "SYN-ACK"));
        datagramPacket = new DatagramPacket(buffer, buffer.length, this.ipClient, this.portClient);
        System.out.println("Sending: SYN-ACK");
        ServerSocket.send(datagramPacket);

        buffer = new byte[BUFFSIZE];
        datagramPacket = new DatagramPacket(buffer, buffer.length);
        ServerSocket.receive(datagramPacket);
        p = Packet.getObject(datagramPacket.getData());
        System.out.println("From Client: "+p.getData());
        System.out.println("Connection established!\n");
    }

    private class RecvAckHandler implements Runnable {

        public synchronized void run() {
            try {
                for ( int i=0; i<PacketCount; i++ ) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    ServerSocket.receive(datagramPacket);
                    Packet p = Packet.getObject(datagramPacket.getData());
                    if ( p.getData().equals("ACK")) {
                        AckBuffer[p.getSeqNumber()] = true;
                    }
                }
                int count = 0;
                for ( int i=0; i<AckBuffer.length; i++ ) {
                    if ( !AckBuffer[i] ) {
                        count++;
                    }
                }
                System.out.println("No of Messages lost: "+count);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendMessageHandler implements Runnable {

        public synchronized void run() {
            try {
                for ( int i=0; i<PacketCount; i++ ) {
                    Packet p = new Packet(i, "This packet: "+i);
                    data[i] = p;
                    byte[] buffer = Packet.getBytes(p);
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, ipClient, portClient);
                    System.out.println("Sending: Packet "+i);
                    ServerSocket.send(datagramPacket);
                }
                System.out.println("\n"+PacketCount+" Packets sended to client!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static Long getCurrentTime() {
        Date d = new Date();
        return d.getTime();
    }

    public static void main(String args[]) throws SocketException, IOException {
        System.out.println("Welcome to Server-Client Chat Application...\n");
        if ( args.length != 2 ) {
            System.out.println("Usage: java Server <Port> <Packet Count>");
            return;
        }
        SimpleUDPServer server = new SimpleUDPServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        new Thread(server.new SendMessageHandler()).start();
        new Thread(server.new RecvAckHandler()).start();
    }

}
