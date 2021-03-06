package br.pucrs.distribuida.p2p;

import com.google.gson.Gson;

import java.util.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class SuperNode {

    public static final String PEER_DATA_CODE = "My data: ";
    public static final String SUPERNODE_REQUEST_CODE = "Looking for resource: ";
    public static final String SUPERNODE_RESPONSE_CODE = "Found resource: ";

    public static final String KEEP_ALIVE = "Keep alive:";
    public static final Integer PORT = 5000;
    public static final Integer STANDARD_ADDITION_TIME = 10;

    private final Lock lock = new ReentrantLock();

    private List<Resource> resources;
    private MulticastSocket multicastSocket;
    private DatagramSocket unicastSocket;
    private DatagramPacket packet;
    private String requestedResource;
    private InetAddress group;
    private Gson gson;
    private Map<String, Integer> registeredIps;

    public SuperNode() throws IOException {
        this.gson = new Gson();
        this.resources = new ArrayList<>();
        this.requestedResource = "";
        multicastSocket = new MulticastSocket(PORT);
        this.group = InetAddress.getByName("230.0.0.1");
        multicastSocket.joinGroup(this.group);
        unicastSocket = new DatagramSocket();
        registeredIps = Collections.synchronizedMap(new HashMap<>());
        byte[] input = new byte[256];
        packet = new DatagramPacket(input, input.length);
    }

    public void run() {
        listenMulticast().start();
        managePeers().start();
    }

    private Thread listenMulticast() {
        return new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("waiting");
                        multicastSocket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Received: " + received);
                        // Se recebe as infos básicas do Peer
                        if (received.contains(PEER_DATA_CODE)) {
                            savePeerInfo(received);
                        }
                        // Se recebe uma requisicão de outro superNodo procurando um arquivo
                        else if (received.contains(SUPERNODE_REQUEST_CODE)) {
                            responseToSupernode(received);
                        }
                        // Se recebe uma resposta de um supernodo informando que achou
                        else if (received.contains(SUPERNODE_RESPONSE_CODE)) {
                            responseToPeer(received);

                        } else if (received.contains(KEEP_ALIVE)) {
                            String ip = received.replace(KEEP_ALIVE, "");
                            addTimestampToPeer(ip);
                        }
                        // Se recebe uma requisição do Peer
                        else {
                            requestToSupernodes(received);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void savePeerInfo(String received) throws IOException {
                String treatedData = received.replace("My data: ", "");
                Resource resource = gson.fromJson(treatedData, Resource.class);
                resources.add(resource);
                registeredIps.put(resource.getIp(), 20);
            }

            public void requestToSupernodes(String received) throws IOException {

                String[] data = received.split(" ");
                String ip = data[data.length - 1];
                for (int i = 0; i < data.length - 1; i++) {
                    String msg = SUPERNODE_REQUEST_CODE + data[i] + "###" + ip;
                    System.out.println(msg);
                    byte[] output = msg.getBytes();
                    DatagramPacket groupPacket = new DatagramPacket(output, output.length, group, PORT);
                    multicastSocket.send(groupPacket);
                }
            }

            public void responseToSupernode(String received) throws IOException {
                String data = received.replace(SUPERNODE_REQUEST_CODE, "");
                System.out.println(data);
                String[] auxArray = data.split("###");
                String resourceName = auxArray[0];
                String ip = auxArray[1];
                if (containsResource(resourceName)) {
                    String msg = SUPERNODE_RESPONSE_CODE + gson.toJson(findResourcesByName(resourceName)) + "###" + ip;
                    byte[] output = msg.getBytes();
                    System.out.println(msg);
                    DatagramPacket groupPacket = new DatagramPacket(output, output.length, group, PORT);
                    multicastSocket.send(groupPacket);
                }
            }

            public void responseToPeer(String received) throws IOException {
                System.out.println(received);
                String[] data = received.replace(SUPERNODE_RESPONSE_CODE, "").split("###");
                Resource[] resourceList = gson.fromJson(data[0], Resource[].class);
                String requestingIp = data[1];
                System.out.println("era pra responder");
                if (registeredIps.containsKey(requestingIp)) {
                    for (Resource resource : resourceList) {
                        System.out.println("respondeu");
                        String msg = String.format("Found resource: %s, with hash: %s, at ip: %s",
                                resource.getFileName(), resource.getHash(), resource.getIp());

                        byte[] output = msg.getBytes();
                        DatagramPacket unicastPacket = new DatagramPacket(
                                output,
                                output.length,
                                InetAddress.getByName(requestingIp),
                                PORT);
                        unicastSocket.send(unicastPacket);

                    }
                }
            }
        };
    }

    private synchronized boolean containsResource(String resourceName) {
        return resources.stream()
                .anyMatch(resource -> resource.getFileName().contains(resourceName));
    }

    private synchronized List<Resource> findResourcesByName(String resourceName) {
        List<Resource> foundResources = new ArrayList<>();

        for (Resource resource : resources) {
            System.out.println(resource.toString());
            if (resource.getFileName().contains(resourceName))
                foundResources.add(resource);
        }

        return foundResources;
    }

    private synchronized void addTimestampToPeer(String ip) {
        System.out.println("adding time to ip " + ip);
        Integer value = registeredIps.get(ip);
        System.out.println("value: " + value);
        if (value != null)
            registeredIps.put(ip, value + STANDARD_ADDITION_TIME);
        else
            registeredIps.put(ip, value);

    }

    private synchronized void decreaseTime() {
        if (!registeredIps.isEmpty()) {
            List<String> keyList = registeredIps.keySet()
                    .stream()
                    .collect(Collectors.toList());

            for (String key : keyList) {
                Integer currentTime = registeredIps.get(key);
                if (currentTime == null)
                    continue;
                currentTime--;
                System.out.println("ip : " + key + "has still : " + currentTime);
                if (currentTime == 0) {
                    System.out.println("ip : " + key + "has now zero");
                    registeredIps.remove(key);
                    resources.removeIf(resource -> resource.getIp().equals(key));
                    continue;
                }
                registeredIps.put(key, currentTime);
            }

        }
    }

    private Thread managePeers() {
        return new Thread() {
            @Override
            public void run() {
                while (true) {
                    decreaseTime();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
    }
}
