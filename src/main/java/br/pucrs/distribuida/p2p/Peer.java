package br.pucrs.distribuida.p2p;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Peer {

    private String ip;
    private String ipSuperNode;
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private List<ResourceStorage> resourceStorage;


    private static final String PATH = "/opt/files";
    private static final String REQUEST_CODE = "Peer would like to download: ";
    private static final String RESPONSE_CODE = "Response from peer ";

    public Peer(String ip, String ipSuperNode) throws IOException {
        this.ip = ip;
        this.resourceStorage = new ArrayList<>();
        this.ipSuperNode = ipSuperNode;
    }

    public void run() throws InterruptedException, IOException, NoSuchAlgorithmException {
        this.receiveSocket = new DatagramSocket(5000);
        this.sendSocket = new DatagramSocket(5001);
        this.declareToSuperNode().start();
        this.receive().start();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Inform the resources you're looking for divided in single white spaces: " +
                    "and if you like to download something type: d-> [resourcename] [hash] [ip] ");
            String msg = scanner.nextLine();
            if (msg.contains("d->")) {
                sendRequestToPeer(msg.replace("d-> ", ""));
            } else
                this.sendMessageToSuperNode(String.format("%s %s ", msg, ip));

        }
    }

    public List<Resource> getFileHashes() throws NoSuchAlgorithmException, FileNotFoundException {
        File folder = new File(PATH);
        File[] listOfFiles = folder.listFiles();
        List<Resource> resourceList = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
                try {
                    Resource resource = new Resource(file.getName(), Hash.hashMessage(file), ip);
                    resourceStorage.add(new ResourceStorage(resource, new String(Files.readAllBytes(file.toPath()))));
                    resourceList.add(resource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public Thread receive() {
        return new Thread(() -> {
            while (true) {
                try {
                    byte[] input = new byte[256];
                    DatagramPacket packet = new DatagramPacket(input, input.length);
                    this.receiveSocket.receive(packet);
                    receiveSocket.getReceiveBufferSize();
                    String received = new String(packet.getData(), 0, packet.getLength());
                    if (received.contains(REQUEST_CODE)) {
                        String[] data = received.replace(REQUEST_CODE, "").split("###");
                        sendResponseToPeer(data[0], data[1], data[2]);
                    } else
                        System.out.println("Received: " + received);
                } catch (SocketTimeoutException e) {
                    System.out.println("Resource(s) not found in network");
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessageToSuperNode(String message) throws IOException {
        byte[] output = message.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(
                output,
                output.length,
                InetAddress.getByName(this.ipSuperNode),
                5000);
        this.sendSocket.send(datagramPacket);
    }

    private void sendRequestToPeer(String request) throws IOException {
        String[] data = request.split(" ");
        String treatedRequest = REQUEST_CODE + data[0] + "###" + data[1] + "###" + ip;
        byte[] output = treatedRequest.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(
                output,
                output.length,
                InetAddress.getByName(data[2]),
                5000);
        this.sendSocket.send(datagramPacket);
    }

    private void sendResponseToPeer(String resourceName, String resourceHash, String peerIp) throws IOException {
        Optional<ResourceStorage> resource = resourceStorage.stream()
                .filter(r -> r.getFileName().equals(resourceName) && r.getHash().equals(resourceHash))
                .findFirst();

        String response = RESPONSE_CODE + ip + ": ";
        if (resource.isPresent()) {
            response = response + new Gson().toJson(resource.get());
        } else {
            response = response + String.format("No resource with name: %s and hash: %s is available at this ip.",
                    resourceName, resourceHash);
        }

        byte[] output = response.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(
                output,
                output.length,
                InetAddress.getByName(peerIp),
                5000);
        this.sendSocket.send(datagramPacket);
    }


    public String getIp() {
        return this.ip;
    }

}
