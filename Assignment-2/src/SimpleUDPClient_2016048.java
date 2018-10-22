// Kaustav Vats (2016048)

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class SimpleUDPClient_2016048 {
    private DatagramSocket ClientSocket;
    private int PacketCount;
    private Packet[] data;
    private boolean[] AckBuffer;
    private boolean DataSend;
    private static final int BUFFSIZE = 65535;

    // Server
    private InetAddress IPAddress;
    private int Port;

    public SimpleUDPClient_2016048(String ipAddress, int portNumber, int packetCount) throws SocketException, IOException {
        this.ClientSocket = new DatagramSocket();
        this.IPAddress = InetAddress.getByName(ipAddress);
        this.Port = portNumber;
        this.PacketCount = packetCount;
        this.data = new Packet[packetCount];
        this.AckBuffer = new boolean[packetCount];
        this.DataSend = false;
    }

    private class RecvAckHandler implements Runnable {

        public synchronized void run() {
            try {
                for ( int i=0; i<PacketCount; i++ ) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    ClientSocket.receive(datagramPacket);
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
                    Packet p = new Packet(i, "This is my Data: "+i);
                    data[i] = p;
                    byte[] buffer = Packet.getBytes(p);
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, Port);
                    System.out.println("Sending, Packet with Seq No: "+i);
                    ClientSocket.send(datagramPacket);
                }
                System.out.println("\n"+PacketCount+" Packets sent to the Server!");
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
        if ( args.length != 3 ) {
            System.out.println("Usage: java Server <ip address> <Port> <Packet Count>");
            return;
        }
        SimpleUDPClient_2016048 client = new SimpleUDPClient_2016048(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        new Thread(client.new SendMessageHandler()).start();
        new Thread(client.new RecvAckHandler()).start();

    }

}
