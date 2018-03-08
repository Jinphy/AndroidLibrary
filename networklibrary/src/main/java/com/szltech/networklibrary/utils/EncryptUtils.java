package com.szltech.networklibrary.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Jinphy on 2018/3/7.
 */

public class EncryptUtils {

    public static final String VIPARA = "0102030405060708";

    /**
     * DESC: AES 加解密秘钥，不同项目可能会不同
     * Created by Jinphy, on 2018/3/8, at 9:02
     */
    public static final String AES_KEY = "OhgkU9HlPbmmXvFpZd2zStk8HfVNHMd4cAbtuNwrpeyUyCMyNFuDHXgiAYBKgcQZNJUatazKWp7eiE4mdmqccQ9ourHF6Hz0WrjrXnbdQxDQ3JCC0i7kgXH6wwWLdSv0";

    /**
     * DESC: 使用默认秘钥加密
     * Created by Jinphy, on 2018/3/8, at 11:12
     */
    public static String aesEncrypt(String content) {


        return encryptHSFund(content);
//        return aesEncrypt(content, AES_KEY);
    }

    /**
     * AES加密
     * @param content 需要加密的内容
     * @param password  加密密码
     * @return
     */
    public static String aesEncrypt(String content, String password) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(StringUtils.strToByteArray(password,16), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(content.getBytes("utf-8"));
            return StringUtils.byteToHexStr(encryptedData); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * DESC: 使用默认的秘钥解密
     * Created by Jinphy, on 2018/3/8, at 11:11
     */
    public static String aesDecrypt(String content) {
        return decryptHSFund(content);
//        return aesDecrypt(content, AES_KEY);
    }


    /**
     * AES解密
     * @param contentStr  待解密内容
     * @param password 解密密钥
     * @return
     * @throws BadPaddingException
     */
    public static String aesDecrypt(String contentStr, String password) {
        try {
            byte[] content  = StringUtils.hexStrToByte(contentStr);
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(StringUtils.strToByteArray(password,16), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(content);
            return new String(decryptedData); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * DESC: MD5 加密
     * Created by Jinphy, on 2018/3/8, at 10:28
     */
    public static String md5Encrypt(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));

            byte[] byteArray = messageDigest.digest();

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                    builder.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                else
                    builder.append(Integer.toHexString(0xFF & byteArray[i]));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * DESC: 华商基金的加密算法
     * Created by Jinphy, on 2018/3/8, at 16:50
     */
    public static String encryptHSFund(String content) {
        int[] iS = new int[256];
        byte[] iK = new byte[256];

        for (int i = 0; i < 256; i++)
            iS[i] = i;

        int j = 1;

        for (short i = 0; i < 256; i++) {
            iK[i] = (byte) AES_KEY.charAt((i % AES_KEY.length()));
        }

        j = 0;

        for (int i = 0; i < 255; i++) {
            j = (j + iS[i] + iK[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
        }

        int i = 0;
        j = 0;
        char[] iInputChar = content.toCharArray();
        char[] tempChars = new char[iInputChar.length];
        for (int x = 0; x < iInputChar.length; x++) {
            i = (i + 1) % 256;
            j = (j + iS[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
            int t = (iS[i] + (iS[j] % 256)) % 256;
            int iY = iS[t];
            char iCY = (char) iY;
            tempChars[x] = (char) (iInputChar[x] ^ iCY);
        }

        String s = new String(tempChars);

        try {
            return StringUtils.byteToHexStr(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * DESC: 华商基金的解密算法
     *
     * Created by Jinphy, on 2018/3/8, at 16:50
     */
    public static String decryptHSFund(String content) {
        try {
            String s = new String(StringUtils.hexStrToByte(content), "UTF-8");

            int[] iS = new int[256];
            byte[] iK = new byte[256];

            for (int i = 0; i < 256; i++)
                iS[i] = i;

            int j = 1;

            for (short i = 0; i < 256; i++) {
                iK[i] = (byte) AES_KEY.charAt((i % AES_KEY.length()));
            }

            j = 0;

            for (int i = 0; i < 255; i++) {
                j = (j + iS[i] + iK[i]) % 256;
                int temp = iS[i];
                iS[i] = iS[j];
                iS[j] = temp;
            }

            int i = 0;
            j = 0;
            char[] iInputChar = s.toCharArray();
            char[] iOutputChar = new char[iInputChar.length];
            for (int x = 0; x < iInputChar.length; x++) {
                i = (i + 1) % 256;
                j = (j + iS[i]) % 256;
                int temp = iS[i];
                iS[i] = iS[j];
                iS[j] = temp;
                int t = (iS[i] + (iS[j] % 256)) % 256;
                int iY = iS[t];
                char iCY = (char) iY;
                iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
            }

            return new String(iOutputChar);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

}
