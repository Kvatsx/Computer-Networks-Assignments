// Kaustav Vats(2016048)
import java.io.IOException;
import java.net.*;
import java.util.Date;

public class Client_2016048 {
    private DatagramSocket ClientSocket;
    private InetAddress ServerIPAddress;
    private int Port;

    private volatile Packet[] data;
    private volatile boolean[] AckReceived;
    private volatile long[] TimeForAck;
    private int PacketCount;
    private int WindowSize;

    private static final int BUFFSIZE = 65535;
    // Helper Variables
    private volatile int AckCount;
    private long PacketDeliveryTime = 500L;

    public Client_2016048(String ipServer, int port, int packetCount, int windowSize) throws SocketException, UnknownHostException {
        this.ClientSocket = new DatagramSocket();
        this.Port = port;
        this.PacketCount = packetCount;
        this.data = new Packet[PacketCount];
        this.AckReceived = new boolean[PacketCount];
        this.TimeForAck = new long[PacketCount];
        this.WindowSize = windowSize;
        this.ServerIPAddress = InetAddress.getByName(ipServer);
        this.AckCount = 0;
    }

    private class SendMessageHandler implements Runnable {

        public void run() {
            try {
                while ( AckCount < PacketCount ) {
                    for ( int i=AckCount; i<=AckCount + WindowSize && i<PacketCount; i++) {
                        if ( getCurrentTime() - TimeForAck[i] >= PacketDeliveryTime && !AckReceived[i] ) {
                            if ( TimeForAck[i] != 0 ) {
                                byte[] buffer = Packet.getBytes(new Packet(i, "This is my Data: "+i));
                                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, ServerIPAddress, Port);
                                System.out.println("Retransmitting, Packet with Seq No: "+i);
                                ClientSocket.send(datagramPacket);
                                TimeForAck[i] = getCurrentTime();
                            }
                            else {
                                byte[] buffer = Packet.getBytes(new Packet(i, "This is my Data: "+i));
                                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, ServerIPAddress, Port);
                                System.out.println("Sending: Packet "+i);
                                ClientSocket.send(datagramPacket);
                                TimeForAck[i] = getCurrentTime();
                            }
                        }
                    }
                }
                System.out.println("All Packets sent!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiverMessageHandler implements Runnable {

        public void run() {
            try {
                while (AckCount < PacketCount) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    ClientSocket.receive(datagramPacket);
                    Packet p = Packet.getObject(datagramPacket.getData());
                    if ( p.getData().equals("ACK") ) {
                        AckReceived[p.getSeqNumber()] = true;
                        System.out.println("ACK received for Packet with Seq No: "+p.getSeqNumber());
                    }
                    for ( int i=AckCount; i<=AckCount + WindowSize && i<PacketCount; i++ ) {
                        if ( !AckReceived[i] ) {
                            break;
                        }
                        AckCount = AckCount + 1;
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static long getCurrentTime() {
        Date d = new Date();
        return d.getTime();
    }

    public static void main(String args[]) throws IOException {
        System.out.println("Welcome to Server-Client Chat Application...\n");
        if ( args.length != 4 ) {
            System.out.println("Usage: java Server <ip address> <Port> <Packet Count> <Window Size>");
            return;
        }
        Client_2016048 client = new Client_2016048(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        new Thread(client.new ReceiverMessageHandler()).start();
        new Thread(client.new SendMessageHandler()).start();
    }

}
