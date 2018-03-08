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
        return aesEncrypt(content, AES_KEY);
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
        return null;
    }

    /**
     * DESC: 使用默认的秘钥解密
     * Created by Jinphy, on 2018/3/8, at 11:11
     */
    public static String aesDecrypt(String content) {
        return aesDecrypt(content, AES_KEY);
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
        return null;
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


}
