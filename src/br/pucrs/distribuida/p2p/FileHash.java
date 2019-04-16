package br.pucrs.distribuida.p2p;

public class FileHash {

    private String fileName;
    private String hash;

    public FileHash(String fileName, String hash) {
        this.setFileName(fileName);
        this.setHash(hash);
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}