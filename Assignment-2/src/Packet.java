// Kaustav Vats (2016048)

import java.io.*;

public class Packet implements Serializable {
    private int seqNumber;
    private String data;

    public Packet(int seq, String mssg) {
        this.seqNumber = seq;
        this.data = mssg;
    }

    public int getSeqNumber() {
        return this.seqNumber;
    }

    public String getData() {
        return this.data;
    }

    public static byte[] getBytes(Packet p) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = null;
        byte[] data = null;
        try {
            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(p);
            objectOutput.flush();
            data = byteArrayOutputStream.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try{
                byteArrayOutputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static Packet getObject(byte[] in) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(in);
        ObjectInput objectInput = null;
        Packet o = null;
        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            o = (Packet) objectInput.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                byteArrayInputStream.close();
                if ( objectInput != null ) {
                    objectInput.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    @Override
    public String toString() {
        return "--------------\n"+"Seq: "+this.seqNumber+"\nData: "+this.data+"\n--------------";
    }
}
