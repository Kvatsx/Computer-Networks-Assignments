// Kaustav Vats(2016048)

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server_2016048 {
    private DatagramSocket ServerSocket;
    private Packet[] data;
    private boolean[] Forwarded;
    private int PacketCount;
    private int WindowSize;

    private static final int BUFFSIZE = 65535;

    public Server_2016048(int Port, int packetCount, int windowSize) throws SocketException {
        this.ServerSocket = new DatagramSocket(Port);
        this.PacketCount = packetCount;
        this.data = new Packet[this.PacketCount];
        this.Forwarded = new boolean[this.PacketCount];
        this.WindowSize = windowSize;
    }

    private class RecvAckHandler implements Runnable {

        public void run() {
            try {
                int deliveryCount = 0;
                while( deliveryCount < PacketCount) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    ServerSocket.receive(datagramPacket);
                    Packet p = Packet.getObject(datagramPacket.getData());
//                    if ( p.getSeqNumber() == 2 ) {
//                        System.out.println("Packet dropped for testing purpose!");
//                        continue;
//                    }
                    if ( p.getSeqNumber() < deliveryCount || p.getSeqNumber() >= (deliveryCount+WindowSize) ) {
                        System.out.println("Flow Error: Packet Discarded!");
                        continue;
                    }
                    else if ( data[p.getSeqNumber()] != null ) {
                        System.out.println("Received duplicate packet with Seq No: "+p.getSeqNumber());
                    }
                    else {
                        data[p.getSeqNumber()] = p;
                    }

                    // Sending ACK for received Packet
                    InetAddress ipClient = datagramPacket.getAddress();
                    int portClient = datagramPacket.getPort();
                    buffer = Packet.getBytes(new Packet(p.getSeqNumber(), "ACK"));
                    System.out.println("Sending ACK for packet with Seq No: "+p.getSeqNumber());
                    datagramPacket = new DatagramPacket(buffer, buffer.length, ipClient, portClient);
                    ServerSocket.send(datagramPacket);

                    for ( int i=deliveryCount; i<PacketCount; i++ ) {
                        if ( !Forwarded[i] && data[i] != null ) {
                            deliveryCount = i+1;
                            System.out.println("Message: \n"+"Seq No: "+data[i].getSeqNumber() +"\n"+"Data: "+data[i].getData()+"\n");
                            Forwarded[i] = true;
                        }
                        if ( data[i] == null ) {
                            break;
                        }
                    }
                }
                System.out.println("All Packets Received!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String args[]) throws IOException, SocketException {
        System.out.println("Welcome to Server-Client Chat Application...\n");
        if ( args.length != 3 ) {
            System.out.println("Usage: java Server <Port> <Packet Count> <Window Size>");
            return;
        }
        Server_2016048 server = new Server_2016048(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        new Thread(server.new RecvAckHandler()).start();
    }

}
