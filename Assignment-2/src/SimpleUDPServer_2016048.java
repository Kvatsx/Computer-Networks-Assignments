// Kaustav Vats (2016048)

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

import static java.lang.Thread.sleep;

public class SimpleUDPServer_2016048 {
    private DatagramSocket ServerSocket;
    private int PacketCount;
    private Packet[] data;
    private boolean[] AckBuffer;
    private boolean DataSend;

    private static final int BUFFSIZE = 65535;

    public SimpleUDPServer_2016048(int Port, int packetCount) throws SocketException, IOException {
        this.ServerSocket = new DatagramSocket(Port);
        this.PacketCount = packetCount;
        this.data = new Packet[packetCount];
        this.AckBuffer = new boolean[packetCount];
        this.DataSend = false;
    }

    private class RecvPacketHandler implements Runnable {

        public synchronized void run() {
            try {
                for ( int i=0; i<PacketCount; i++ ) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    ServerSocket.receive(datagramPacket);
                    Packet p = Packet.getObject(datagramPacket.getData());

                    InetAddress ipClient = datagramPacket.getAddress();
                    int portClient = datagramPacket.getPort();
                    data[p.getSeqNumber()] = p;
                    System.out.println("\nReceived Packet: "+p.getSeqNumber()+"\nData: "+p.getData());
                    buffer = Packet.getBytes(new Packet(p.getSeqNumber(), "ACK"));
                    datagramPacket = new DatagramPacket(buffer, buffer.length, ipClient, portClient);
                    System.out.println("Sending: ACK "+p.getSeqNumber());
                    ServerSocket.send(datagramPacket);
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
        if ( args.length != 2 ) {
            System.out.println("Usage: java Server <Port> <Packet Count>");
            return;
        }
        SimpleUDPServer_2016048 server = new SimpleUDPServer_2016048(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        new Thread(server.new RecvPacketHandler()).start();
    }

}
