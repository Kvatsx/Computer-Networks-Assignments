// Kaustav Vats (2016048)

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class SimpleUDPClient {
    private DatagramSocket ClientSocket;
    private int PacketCount;
    private Packet[] data;
    private boolean[] AckBuffer;
    private boolean DataSend;
    private static final int BUFFSIZE = 65535;

    // Server
    private InetAddress IPAddress;
    private int Port;

    public SimpleUDPClient(String ipAddress, int portNumber, int packetCount) throws SocketException, IOException {
        this.ClientSocket = new DatagramSocket();
        this.IPAddress = InetAddress.getByName(ipAddress);
        this.Port = portNumber;
        this.PacketCount = packetCount;
        this.data = new Packet[packetCount];
        this.DataSend = false;

        newConnectionHandler();
    }

    public void newConnectionHandler() throws IOException {
        byte[] buffer = Packet.getBytes(new Packet(0, "SYN"));
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, Port);
        System.out.println("Sending: SYN");
        ClientSocket.send(datagramPacket);

        buffer = new byte[BUFFSIZE];
        datagramPacket = new DatagramPacket(buffer, buffer.length);
        ClientSocket.receive(datagramPacket);
        Packet p = Packet.getObject(datagramPacket.getData());
        System.out.println("From Server: " + p.getData());

        buffer = Packet.getBytes(new Packet(0, "ACK"));
        datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, Port);
        System.out.println("Sending: ACK");
        ClientSocket.send(datagramPacket);

        System.out.println("Connection established!\n");
    }

    private class RecvPacketHandler implements Runnable {

        public synchronized void run() {
            try {
                for ( int i=0; i<PacketCount; i++ ) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    ClientSocket.receive(datagramPacket);
                    Packet p = Packet.getObject(datagramPacket.getData());

                    data[p.getSeqNumber()] = p;
                    System.out.println("\nReceived Packet: "+p.getSeqNumber()+"\nData: "+p.getData());
                    buffer = Packet.getBytes(new Packet(p.getSeqNumber(), "ACK"));
                    datagramPacket = new DatagramPacket(buffer, buffer.length, IPAddress, Port);
                    System.out.println("Sending: Ack "+p.getSeqNumber());
                    ClientSocket.send(datagramPacket);
                }
                int count = 0;
                for ( int i=0; i<PacketCount; i++ ) {
                    if ( data[i] == null ) {
                        count++;
                    }
                }
                System.out.println("\nPackets Received: " + (PacketCount-count));
                System.out.println("Packets Lost: "+count);
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
        SimpleUDPClient client = new SimpleUDPClient(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        new Thread(client.new RecvPacketHandler()).start();
    }

}
