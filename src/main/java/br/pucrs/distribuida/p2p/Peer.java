package br.pucrs.distribuida.p2p;

import com.google.gson.Gson;

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
    private String ipSuperNode;
    private DatagramSocket socket;
    private DatagramPacket packet;

    private static final String PATH = "/opt/files";

    public Peer(String ip, String fileName, String hash, String ipSuperNode) throws IOException {
        this.ip = ip;
        this.fileName = fileName;

        this.ipSuperNode = ipSuperNode;
    }

    public void run() throws InterruptedException, IOException, NoSuchAlgorithmException {
        this.socket = new DatagramSocket(5000);
        this.declareToSuperNode().start();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            //System.out.println("Inform the resources you're looking for divided in single white spaces: ");
            String msg = scanner.nextLine() + " " + ip;
            this.sendMessageToSuperNode(msg);
            byte[] input = new byte[256];
            DatagramPacket packet = new DatagramPacket(input, input.length);
            try {
                this.socket.setSoTimeout(3000);
                this.socket.receive(packet);
                socket.getReceiveBufferSize();
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);
            } catch (SocketTimeoutException e) {
                System.out.println("Resource(s) not found in network");
            }
        }
    }

    public List<Resource> getFileHashes() throws NoSuchAlgorithmException, FileNotFoundException {
        File folder = new File(PATH);
        File[] listOfFiles = folder.listFiles();
        List<Resource> resourceList = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
                resourceList.add(new Resource(file.getName(), Hash.hashMessage(file), ip));
            }
        }
        return resourceList;
    }

    public Thread declareToSuperNode() {
        return new Thread(() -> {
            try {
                for (Resource resource : getFileHashes()) {
                    sendMessageToSuperNode("My data: " + new Gson().toJson(resource));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Thread.sleep(5000);
                    sendMessageToSuperNode("Keep alive:" + ip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    @Override
    public String toString() {
        return "My data: " + ip + "-" + fileName;
    }

    public String getIp() {
        return this.ip;
    }

}
