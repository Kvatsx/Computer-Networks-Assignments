// Kaustav Vats (2016048)

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class Server {
    private DatagramSocket serverSocket;
    private int PORT;
    private static final int BUFFSIZE = 65535;
    private int windowSize;

    private volatile int PacketCount;
    private volatile int NextSeqNumber;
    private Packet[] data;

    private volatile Long[] timeForACK;
    private static final Long RTT = 1000L; // RTT in ms

    // Client
    private InetAddress ipClient;
    private int portClient;

    public Server(int portNumber, int winsize) throws SocketException, IOException{
        this.serverSocket = new DatagramSocket(portNumber);
        this.PORT = portNumber;
        this.windowSize = winsize;
        this.PacketCount = 1000;
        this.NextSeqNumber = 0;
        this.timeForACK = new Long[3000];
        this.data = new Packet[this.PacketCount];

        for ( int i=0; i<3000; i++ ) {
            timeForACK[i] = 0L;
        }
        this.ipClient = null;
        this.portClient = 0;

        newConnectionHandler();
    }

    public void newConnectionHandler() throws IOException {
        byte[] buffer = new byte[BUFFSIZE];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(datagramPacket);
        Packet p = Packet.getObject(datagramPacket.getData());
        System.out.println("From Client: "+p.getData());

        this.ipClient = datagramPacket.getAddress();
        this.portClient = datagramPacket.getPort();
        buffer = Packet.getBytes(new Packet(0, "SYN-ACK"));
        datagramPacket = new DatagramPacket(buffer, buffer.length, this.ipClient, this.portClient);
        System.out.println("Sending: SYN-ACK");
        serverSocket.send(datagramPacket);

        buffer = new byte[BUFFSIZE];
        datagramPacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(datagramPacket);
        p = Packet.getObject(datagramPacket.getData());
        System.out.println("From Client: "+p.getData());
        System.out.println("Connection established!\n");
    }


    private class RecvAckHandler implements Runnable {

        public synchronized void run() {
            try {
                while (NextSeqNumber < PacketCount) {
                    byte[] buffer = new byte[BUFFSIZE];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(datagramPacket);
                    Packet p = Packet.getObject(datagramPacket.getData());
                    if ( p.getData().equals("ACK") ) {
                        if ( p.getSeqNumber() > NextSeqNumber ) {
                            NextSeqNumber = p.getSeqNumber();
                            System.out.println("From Client: Correctly received upltil "+(p.getSeqNumber()-1));
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class SendMessageHandler implements Runnable {

        public synchronized void run() {
            try {
                while(NextSeqNumber < PacketCount) {
                    for ( int i=NextSeqNumber; i<=NextSeqNumber+windowSize && i<PacketCount; i++ ) {
                        if ( getCurrentTime() - timeForACK[i] >= RTT ) {
                            byte[] buffer = Packet.getBytes(new Packet(i, "Packet: "+i));
                            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, ipClient, portClient);
                            System.out.println("Sending: Packet "+i);
                            serverSocket.send(datagramPacket);
                            timeForACK[i] = getCurrentTime();
                        }
                    }
                }
                System.out.println("All Packets send!");
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

    private String UserInput() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        return in.readLine();
    }

    public static void main(String args[]) throws IOException, SocketException {
        System.out.println("Welcome to Server-Client Chat Application...\n");
        if ( args.length != 2 ) {
            System.out.println("Usage: java Server <Port> <Window Size>");
            return;
        }
        Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        new Thread(server.new SendMessageHandler()).start();
        new Thread(server.new RecvAckHandler()).start();
    }

}