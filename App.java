
public class App {
    public static void main(String[] args) {
        Role role = Role.SuperNode;
        String ip = args[0];
        if (args.length > 1) {
            role = Role.Node;
            String fileName = args[1];
            new Peer(ip, fileName, null);
        } else {
            new SuperNode(ip);
        }
        System.out.println(role);
        System.out.println(ip);
    }
}