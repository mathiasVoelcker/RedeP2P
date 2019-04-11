
public class App {
    public static void main(String[] args) {
        Role role = Role.SuperNode;
        String ip = "";
        if (args.length > 0) {
            role = Role.Node;
            ip = args[0];
        }
        System.out.println(role);
        System.out.println(ip);
    }
}