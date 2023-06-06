package com.example.newdemo;

import android.os.Build;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashGenerationUtils {

    /*
     * Do not use this, you may use this only for testing.
     * This should be done from the server side.
     * Do not keep salt anywhere in the app.
     */
    public static String generateHashFromSDK(String hashData, String salt, String merchantSecretKey) {
        if (merchantSecretKey == null || merchantSecretKey.isEmpty()) {
            return calculateHash(hashData + salt);
        } else {
            return calculateHmacSha1(hashData, merchantSecretKey);
        }
    }

    public static String generateV2HashFromSDK(String hashString, String salt) {
        return calculateHmacSha256(hashString, salt);
    }

    /**
     * Function to calculate the SHA-512 hash
     * @param hashString hash string for hash calculation
     * @return Post Data containing the hash
     */
    private static String calculateHash(String hashString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            return getHexString(mdbytes);
        } catch (Exception e) {
            return null;
        }
    }

    private static String calculateHmacSha1(String hashString, String key) {
        try {
            String type = "HmacSHA1";
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
            Mac mac = Mac.getInstance(type);
            mac.init(secret);
            byte[] bytes = mac.doFinal(hashString.getBytes());
            return getHexString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getHexString(byte[] data) {
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : data) {
            String h = Integer.toHexString(0xFF & aMessageDigest);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
    }

    private static String calculateHmacSha256(String hashString, String salt) {
        try {
            String type = "HmacSHA256";
            SecretKeySpec secret = new SecretKeySpec(salt.getBytes(), type);
            Mac mac = Mac.getInstance(type);
            mac.init(secret);
            byte[] bytes = mac.doFinal(hashString.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            return null;
        }
        return hashString;
    }
}

