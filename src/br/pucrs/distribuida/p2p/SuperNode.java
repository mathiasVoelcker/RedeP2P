package br.pucrs.distribuida.p2p;
import java.util.Map;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class SuperNode {

    private String ip;
    private Map<String, Peer> peers;
    private MulticastSocket multicastSocket;
    private DatagramSocket unicastSocket;
    private DatagramPacket packet;
    private boolean awaitingResponse = false;
    private InetAddress address;
    private InetAddress group;

    public SuperNode(String ip) throws IOException {
        this.ip = ip;
        this.peers = new HashMap<String, Peer>();
        this.awaitingResponse = false;
        this.address = InetAddress.getByName(ip);
        multicastSocket = new MulticastSocket(Integer.parseInt(ip));
        this.group = InetAddress.getByName("230.0.0.1");
        multicastSocket.joinGroup(this.group);
        // unicastSocket = new DatagramSocket(Integer.parseInt(ip));
        byte[] input = new byte[256];
        packet = new DatagramPacket(input, input.length);
    }

    public void run() {
        // listenUnicast().start();
        listenMulticast().start();
    }

    public void AddPeer(Peer peer) {
        peers.put(peer.getIp(), peer);
    }

    private Thread listenMulticast() {
        return new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("waiting");
                        multicastSocket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Received: " + received);
                        //Se recebe as infos básicas do Peer
                        if (received.contains("My data: ")) {
                            String[] peerData = received.replace("My data: ", "").split("-");
                            Peer receivedPeer = new Peer(peerData[0], peerData[1], peerData[2], ip);
                            peers.put(receivedPeer.getIp(), receivedPeer);
                        }
                        //Se recebe uma requisicão de outro superNodo procurando um arquivo
                        else if (received.contains("Looking for:")) {
                            System.out.println(received);
                        } 
                        //Se recebe uma requisição do Peer
                        else {
                            received = "Looking for: " + received;
                            byte[] output = received.getBytes();
                            DatagramPacket groupPacket = new DatagramPacket(output, output.length, group, 3000);
                            multicastSocket.send(groupPacket);
                            awaitingResponse = true;
                        }
                        System.out.println(peers.values().size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    // private Thread listenMulticast() {
    //     return new Thread() {
    //         @Override
    //         public void run() {
    //             while (true) {
    //                 try {
    //                     System.out.println("waiting");
    //                     multicastSocket.receive(packet);
    //                     String received = new String(packet.getData(), 0, packet.getLength());
    //                     System.out.println("Received in multicast: " + received);
    //                 } catch (IOException e) {
    //                     e.printStackTrace();
    //                 }
    //             }
    //         }
    //     };
    // }

    public String getIp() {
        return this.ip;
    }
}