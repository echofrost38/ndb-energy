package com.ndb.auction.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utilities {
    
    @Value("${encrypt.key}")
    private static String key;

    @Value("${encrypt.initVector}")
    private static String initVector;

    // locktime format
    public static String lockTimeFormat(int seconds) {
        int sec = seconds % 60;
        int min = Math.floorDiv((seconds % 3600) / 60, Integer.MIN_VALUE);
        int hours = Math.floorDiv(seconds / 3600, Integer.MIN_VALUE);
        String formatted = "";
        if(hours > 0) formatted += String.format("%dhr ", hours);
        if(min > 0) formatted += String.format("%dm ", min);
        formatted += String.format("%ds", sec);
        return formatted;
    }

    public static String encrypt(final String strToEncrypt) {
        try {
            var iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            var skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            var cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());    
            
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            log.info("Error while encrypting: " + e.toString());
        } 
        return null;
    }

    public static String decrypt(String strToDecrypt) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(strToDecrypt));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
}
