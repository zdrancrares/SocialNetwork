package com.example.socialnetworkgui;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Main {
    public static String encryptPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public static void main(String[] args) {
        try {
            String password = "mySecretPasswOrd2";
            String encryptedPassword = encryptPassword(password);
            System.out.println("Parola criptată: " + encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



