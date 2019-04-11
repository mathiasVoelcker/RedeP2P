import java.util.List;
import java.io.IOException;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class SuperNode {

    private String Ip;
    List<Peer> Nodes;
    private MulticastSocket socket;

    public SuperNode(String ip) throws IOException {
        this.Ip = ip;
        this.Nodes = new ArrayList<Peer>();
        socket = new MulticastSocket(5000);
    }
}