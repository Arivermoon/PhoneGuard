package com.self.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tanlang on 2016-05-09.
 */
public class Md5Utils {

    public static String md5(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toHexString(bytes[i] & 0xFF));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

}
