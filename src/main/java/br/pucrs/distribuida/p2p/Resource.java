package br.pucrs.distribuida.p2p;

public class Resource {

    private String fileName;
    private String hash;
    private String ip;


    public Resource(String fileName, String hash, String ip) {
        this.fileName = fileName;
        this.hash = hash;
        this.ip = ip;
    }

    public Resource() {

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "fileName='" + fileName + '\'' +
                ", hash='" + hash + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}