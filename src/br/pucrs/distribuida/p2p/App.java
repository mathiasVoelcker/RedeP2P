package br.pucrs.distribuida.p2p;
import java.io.IOException;
import java.util.Scanner;


/*Para rodar a aplicação como superNode, basta digitar:
    java App [ip]

Para rodar a aplicação como Peer, basta digitar
    Java App [ipSuperNodo] [fileName] [ipPeer]

Para um Peer solicitar ip de uma outra Peer, basta escrever o hash ou nome do arquivo da Peer desejada
*/
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        String ipSuperNode = args[0];
        if (args.length > 1) {
            System.out.println("Peer selected");
            String fileName = args[1];
            String ip = args[2];
            Peer peer = new Peer(ip, fileName, fileName, ipSuperNode);
            peer.run();
        } else {
            System.out.println("SuperNode selected");
            new SuperNode(ipSuperNode).run();
        }
    }
}
