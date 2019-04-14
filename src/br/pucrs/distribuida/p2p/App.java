package br.pucrs.distribuida.p2p;

import java.io.IOException;
import java.util.Scanner;


/*Para rodar a aplicação como superNode, basta digitar:
    java App [ip]

Para rodar a aplicação como Peer, basta digitar
    Java App [ipSuperNodo] [fileName] [ipPeer]
*/
public class App {
    public static void main(String[] args) throws IOException {
        Role role = Role.SuperNode;
        String ipSuperNode = args[0];
        if (args.length > 1) {
            System.out.println("Peer selected");
            role = Role.Node;
            String fileName = args[1];
            String ip = args[2];
            Peer peer = new Peer(ip, fileName, null, ipSuperNode);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("What do you want to say?");
                peer.sendMessageToSuperNode(scanner.nextLine());
            }

        } else {
            System.out.println("SuperNode selected");
            new SuperNode(ipSuperNode).run();
        }
    }
}
