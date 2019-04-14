package br.pucrs.distribuida.p2p;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static byte[] hashMessage(String message) throws NoSuchAlgorithmException {
        byte[] messageBytes = message.getBytes();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(messageBytes);
        return messageDigest.digest();

    }
}
