package com.szltech.networklibrary.utils;

/**
 *
 * Created by Jinphy on 2018/3/7.
 */

public class StringUtils {


    public static boolean equal(String one, String two) {
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }

    public static boolean empty(Object value) {
        return value == null || value.toString().length() == 0;
    }

    public static boolean trimEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }


    /**
     *  将任意字符串转换为capacity个元素的byte数组
     *  长度超过32位的将被截断，不够32位的将被循环填充进数组
     *  @param value  源字符串
     *  @param capacity      数组的容量
     *  @return Byte数组
     *  入参的串只能是ASCII码值，不能含有中文
     */
    public static byte[] strToByteArray(String value,int capacity){
        if(value==null || value.equals("")){
            return new byte[capacity];
        }else{
            char [] tempStr = value.toCharArray();
            byte [] resultByteArr = new byte[capacity];
            int count = capacity / value.length();
            int mod = capacity % value.length();
            //判断是否需要循环填充数组
            //需要循环
            if (count > 0) {
                for (int i = 0; i < capacity; i++) {
                    byte tempByte;
                    //判断，是否已经遍历一次
                    int flag = i / value.length();
                    //如果已经遍历了一次，取余数作为下标
                    if (flag > 0) {
                        tempByte = (byte) tempStr[i%value.length()];
                        resultByteArr[i] = tempByte;
                    }else{
                        //如果没有遍历结束，取i作为下标
                        tempByte = (byte) tempStr[i];
                        resultByteArr[i] = (byte) tempStr[i];
                    }
                }
                //不需要循环
            } else if(mod > 0) {
                for (int i = 0; i < capacity; i++) {
                    Byte tempChar = (byte) tempStr[i];
                    resultByteArr[i] = tempChar;
                }
            }
            return resultByteArr;
        }
    }




    /**将二进制转换成16进制
     * @param bytes
     * @return
     */
    public static String byteToHexStr(byte bytes[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }


    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] hexStrToByte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
