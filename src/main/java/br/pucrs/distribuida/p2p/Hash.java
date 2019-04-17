package br.pucrs.distribuida.p2p;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String hashMessage(File file) throws Exception {
        try (InputStream input = new FileInputStream(file)) {
            return DigestUtils.md5Hex(input);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}

