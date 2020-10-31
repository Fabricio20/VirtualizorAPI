package net.notfab.virtualizor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class KeyGenerator {

    public static String get(String password) {
        return generate(generateRandStr(), password);
    }

    private static String generate(String key, String pass) {
        return key + md5(pass + key);
    }

    private static String generateRandStr() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int rand = new Random().nextInt(61);
            if (rand < 10) {
                token.append((char) (rand + 48));
            } else if (rand < 36) {
                token.append((char) (rand + 55));
            } else {
                token.append((char) (rand + 61));
            }
        }
        return token.toString().toLowerCase();
    }

    private static String md5(String md5) {
        try {
            MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
