public class Peer {

    private String Ip;
    private String FileName;
    private String[] Hash;

    public Peer(String ip, String fileName, String[] hash) {
        this.Ip = ip;
        this.FileName = fileName;
        this.Hash = hash;
    }
}
