package br.pucrs.distribuida.p2p;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/*Para rodar a aplicação como superNode, basta digitar:
    java App [ip]

Para rodar a aplicação como Peer, basta digitar
    Java App [ipSuperNodo] [ipPeer]

Para um Peer solicitar ip de uma outra Peer, basta escrever o hash ou nome do arquivo da Peer desejada
*/
public class App {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {
        if (args.length == 0) {
            System.out.println("Not enough arguments.");
            System.exit(1);
        }
        String ipSuperNode = args[0];
        if (args.length > 1) {
            System.out.println("Peer selected");
            String ip = args[1];
            Peer peer = new Peer(ip, ipSuperNode);
            peer.run();
        } else {
            System.out.println("SuperNode selected");
            new SuperNode().run();
        }
    }
}
