package br.pucrs.distribuida.p2p;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Peer {

    private String ip;
    private String fileName;
    private String hash;
    private String ipSuperNode;
    private DatagramSocket socket;
    private DatagramPacket packet;

    public Peer(String ip, String fileName, String hash, String ipSuperNode) throws IOException {
        this.ip = ip;
        this.fileName = fileName;
        this.hash = hash;
        this.ipSuperNode = ipSuperNode;
    }

    public void run() throws InterruptedException, IOException, NoSuchAlgorithmException {
        this.socket = new DatagramSocket(5000);
        this.declareToSuperNode().start();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("What are you looking for");
            String msg = scanner.nextLine() + "-IP: " + ip;
            this.sendMessageToSuperNode(msg);
            byte[] input = new byte[256];
            DatagramPacket packet = new DatagramPacket(input, input.length);
            try {
                this.socket.setSoTimeout(3000);
                this.socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);
            } catch (SocketTimeoutException e) {
                System.out.println("Hash not found in network");
            }
        }
    }

    public List<FileHash> getFileHashes() throws NoSuchAlgorithmException, FileNotFoundException {
        File folder = new File("/opt/files");
        File[] listOfFiles = folder.listFiles();
        List<FileHash> fileHashList = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
                fileHashList.add(new FileHash(file.getName(), Hash.hashMessage(file)));
            }
        }
        return fileHashList;
    }

    public Thread declareToSuperNode() throws InterruptedException, IOException {
        String msg = this.toString();
        return new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        for (FileHash fileHash : getFileHashes()) {
                            sendMessageToSuperNode("My data: " + ip + "-" + fileHash.getFileName() + "-" + fileHash.getHash());
                        }
                        Thread.sleep(20000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void sendMessageToSuperNode(String message) throws IOException {
        byte[] output = message.getBytes();
        this.packet = new DatagramPacket(
                output,
                output.length,
                InetAddress.getByName(this.ipSuperNode),
                5000);
        this.socket.send(packet);
    }

    // public String toString() {
    //     return "My data: " + ip + "-" + fileName + "-" + hash;
    // }

    public String getIp() {
        return this.ip;
    }

    public String getHash() {
        return this.hash;
    }
}
