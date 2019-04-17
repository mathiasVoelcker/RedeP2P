package br.pucrs.distribuida.p2p;

public class ResourceStorage extends Resource {
    private String fileContent;


    public ResourceStorage(String fileName, String hash, String ip, String fileContent) {
        super(fileName, hash, ip);
        this.fileContent = fileContent;
    }

    public ResourceStorage(Resource resource, String fileContent){
        super(resource.getFileName(), resource.getHash(), resource.getIp());
        this.fileContent = fileContent;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }


}
