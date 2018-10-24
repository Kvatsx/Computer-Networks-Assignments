// Kaustav Vats (2016048)

import java.io.IOException;
import java.net.*;

public class SimpleUDPServer_2016048 {
    private DatagramSocket ServerSocket;
    private int PacketCount;
    private Packet[] data;
    private static final int BUFFSIZE = 65535;

    public SimpleUDPServer_2016048(int Port, int packetCount) throws IOException {
        this.ServerSocket = new DatagramSocket(Port);
        this.PacketCount = packetCount;
        this.data = new Packet[packetCount];
    }

    private void RecvPacketHandler() {
        try {
            for ( int i=0; i<PacketCount; i++ ) {
                byte[] buffer = new byte[BUFFSIZE];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                ServerSocket.receive(datagramPacket);
                ServerSocket.setSoTimeout(1000);
                Packet p = Packet.getObject(datagramPacket.getData());
                data[p.getSeqNumber()] = p;
                System.out.println("\nReceived Packet: "+p.getSeqNumber()+"\nData: "+p.getData());
            }
        }
        catch (SocketTimeoutException s) {
            System.out.println("Timeout!!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            int count = 0;
            for ( int i=0; i<PacketCount; i++ ) {
                if ( data[i] == null ) {
                    count++;
                }
            }
            System.out.println("\nPackets Received: " + (PacketCount-count));
            System.out.println("Packets Lost: "+count);
        }
    }

    public static void main(String args[]) throws IOException {
        System.out.println("Welcome to Server-Client Chat Application...\n");
        if ( args.length != 2 ) {
            System.out.println("Usage: java Server <Port> <Packet Count>");
            return;
        }
        SimpleUDPServer_2016048 server = new SimpleUDPServer_2016048(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        server.RecvPacketHandler();
    }

}
