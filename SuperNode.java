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
    private InetAddress address;

    public SuperNode(String ip) throws IOException {
        this.ip = ip;
        this.peers = new HashMap<String, Peer>();
        this.address = InetAddress.getByName(ip);
        multicastSocket = new MulticastSocket(5001);
        unicastSocket = new DatagramSocket(5000);
        // multicastSocket.bind(new InetSocketAddress(ip, 5001));
        // unicastSocket.bind(new InetSocketAddress(ip, 5000));
        byte[] input = new byte[256];
        packet = new DatagramPacket(input, input.length);
    }
    
    public void run() {
        listenUnicast().start();
        listenMulticast().start();
    }

    public void AddPeer(Peer peer) {
        peers.put(peer.getIp(), peer);
    }

    private Thread listenUnicast() {
        return new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("waiting");
                        unicastSocket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Received: "+ received);
                        if (received.contains("My data: ")) {
                            String[] peerData = received.replace("My data: ", "").split("-");
                            Peer receivedPeer = new Peer(peerData[0], peerData[1], peerData[2], ip);
                            peers.put(receivedPeer.getIp(), receivedPeer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
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
		                System.out.println("Received in multicast: "+ received);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

	public String getIp() {
		return this.ip;
	}
}