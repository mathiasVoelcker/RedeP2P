
package br.pucrs.distribuida.p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Peer {

    private String ip;
    private String fileName;
    private String hash;
    private String ipSuperNode;
    private DatagramSocket socket;
    private DatagramPacket packet;

    public Peer(String ip, String fileName, String hash, String ipSuperNode) throws IOException {
        this.ip = ip;
        this.fileName = fileName;
        this.hash = hash;
        this.ipSuperNode = ipSuperNode;
        this.socket = new DatagramSocket();
    }

    public Thread declareToSuperNode() throws InterruptedException, IOException {
        String msg = this.toString();
        return new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sendMessageToSuperNode(msg);
                        Thread.sleep(15000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void sendMessageToSuperNode(String message) throws IOException {
        byte[] output = message.getBytes();
        this.packet = new DatagramPacket(
                output,
                output.length,
                InetAddress.getByName(this.ipSuperNode),
                5000);
        this.socket.send(packet);
    }

    public String toString() {
        return "My data: " + ip + "-" + fileName + "-" + hash;
    }

    public String getIp() {
        return this.ip;
    }
}
