package br.pucrs.distribuida.p2p;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static String hashMessage(File file) throws NoSuchAlgorithmException, FileNotFoundException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream fileIn = new FileInputStream(file);
            DigestInputStream digestIn = new DigestInputStream(fileIn, digest);

            // Converte bytes para hex
            StringBuilder result = new StringBuilder();
            for (byte b : digestIn.getMessageDigest().digest()) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch(NoSuchAlgorithmException ex){
            System.err.println(ex.getMessage());
        }
        return null;
    }
}

