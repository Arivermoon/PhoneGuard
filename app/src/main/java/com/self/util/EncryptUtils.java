package com.self.util;

import com.self.constant.Constant;

/**
 * Created by tanlang on 2016-05-12.
 */
public class EncryptUtils {

    public static String encrypt(String args) {
        byte[] bytes = args.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= Constant.SIGN;
        }
        return new String(bytes);
    }

    public static String decrypt(String args) {
        byte[] bytes = args.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= Constant.SIGN;
        }
        return new String(bytes);
    }

}
