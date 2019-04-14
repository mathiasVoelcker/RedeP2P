package br.pucrs.distribuida.p2p;
import java.util.Map;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class SuperNode {

    public static String PEER_DATA_CODE = "My data: ";
    public static String SUPERNODE_REQUEST_CODE = "Looking for hash: ";
    public static String SUPERNODE_RESPONSE_CODE = "Found hash: ";
    public static String PEER_RESPONSE = "Found Ip: ";

    private Map<String, Peer> peers;
    private MulticastSocket multicastSocket;
    private DatagramSocket unicastSocket;
    private DatagramPacket packet;
    private String requestedHash;
    private String requestingIp;
    private InetAddress group;
    private String ip;

    public SuperNode(String ip) throws IOException {
        this.ip = ip;
        this.peers = new HashMap<String, Peer>();
        this.requestedHash = "";
        multicastSocket = new MulticastSocket(5000);
        this.group = InetAddress.getByName("230.0.0.1");
        multicastSocket.joinGroup(this.group);
        unicastSocket = new DatagramSocket();
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
                        // Se recebe as infos básicas do Peer
                        if (received.contains(PEER_DATA_CODE)) {
                            savePeerInfo(received);
                        }
                        // Se recebe uma requisicão de outro superNodo procurando um arquivo
                        else if (received.contains(SUPERNODE_REQUEST_CODE)) {
                            responseToSupernode(received);
                        }
                        // Se recebe uma resposta de um supernodo informando que achou
                        else if (received.contains(SUPERNODE_RESPONSE_CODE)) {
                            responseToPeer(received);
                        }
                        // Se recebe uma requisição do Peer
                        else {
                            requestToSupernodes(received);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void savePeerInfo(String received) throws IOException {
                String[] peerData = received.replace("My data: ", "").split("-");
                Peer receivedPeer = new Peer(peerData[0], peerData[1], peerData[2], ip);
                peers.put(receivedPeer.getHash(), receivedPeer);
            }

            public void requestToSupernodes(String received) throws IOException {
                String[] data = received.split("-IP: ");
                String hash = data[0];
                String msg = SUPERNODE_REQUEST_CODE + hash;
                byte[] output = msg.getBytes();
                DatagramPacket groupPacket = new DatagramPacket(output, output.length, group, 5000);
                multicastSocket.send(groupPacket);
                requestedHash = hash;
                requestingIp = data[1];
            }

            public void responseToSupernode(String received) throws IOException {
                String hash = received.replace(SUPERNODE_REQUEST_CODE, "");
                if (peers.containsKey(hash)) {
                    String msg = SUPERNODE_RESPONSE_CODE + hash + "-IP: " + peers.get(hash).getIp();
                    byte[] output = msg.getBytes();
                    DatagramPacket groupPacket = new DatagramPacket(output, output.length, group, 5000);
                    multicastSocket.send(groupPacket);
                }
            }

            public void responseToPeer(String received) throws IOException {
                String hash = received.replace(SUPERNODE_RESPONSE_CODE, "").split("-")[0];
                if (requestedHash.equals(hash)) {
                    String ip = received.split("-IP: ")[1];
                    String msg = PEER_RESPONSE + ip;
                    byte[] output = ip.getBytes();
                    DatagramPacket unicastPacket = new DatagramPacket(
                                    output,
                                    output.length,
                                    InetAddress.getByName(requestingIp),
                                    5000);
                    unicastSocket.send(unicastPacket);
                }
                requestingIp = "";
                requestedHash = "";
            }
        };
    }

    public String getIp() {
        return this.ip;
    }
}
