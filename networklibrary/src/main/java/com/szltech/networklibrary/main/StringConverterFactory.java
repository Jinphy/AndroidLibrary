package com.szltech.networklibrary.main;

import android.content.Context;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.szltech.networklibrary.utils.EncryptUtils;
import com.szltech.networklibrary.utils.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 *
 * Created by Jinphy on 2018/3/7.
 */

public class StringConverterFactory extends Converter.Factory {
    private WeakReference<Context> context;
    private String url;
    private boolean useCache;

    public static StringConverterFactory create(WeakReference<Context> context, String url, boolean useCache) {
        return new StringConverterFactory(context, url, useCache);
    }

    private StringConverterFactory(WeakReference<Context> context, String url, boolean useCache) {
        this.context = context;
        this.url = url;
        this.useCache = useCache;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,Retrofit retrofit) {
        return new StringResponseConverter(context, url, useCache);
    }


    /**
     * DESC: 字符串转换器
     * Created by Jinphy, on 2018/3/8, at 8:58
     */
    public static class StringResponseConverter implements Converter<ResponseBody, String> {

        private final WeakReference<Context> context;
        private final String url;
        private final boolean useCache;

        public StringResponseConverter(WeakReference<Context> context, String url, boolean useCache){
            this.context = context;
            this.url = url;
            this.useCache = useCache;
        }



        @Override
        public String convert(ResponseBody value) throws IOException {

                String data = tempDecrypt(value.string(), EncryptUtils.AES_KEY);
                value.close();
//            String data = EncryptUtils.aesDecrypt(value.string(), EncryptUtils.AES_KEY);
//            value.close();
//            if (useCache && !TextUtils.isEmpty(url) && ObjectUtils.reference(context)) {
//                Cache.save(context.get(), url, data);
//            }
            LogUtils.e(data);
            return data;
        }


        /**
         * DESC: 华商基金的解密算法
         * Created by Jinphy, on 2018/3/8, at 16:07
         */
        private String tempDecrypt(String data, String key) {
            try {
                String s = new String(StringUtils.hexStrToByte(data), "UTF-8");

                int[] iS = new int[256];
                byte[] iK = new byte[256];

                for (int i = 0; i < 256; i++)
                    iS[i] = i;

                int j = 1;

                for (short i = 0; i < 256; i++) {
                    iK[i] = (byte) key.charAt((i % key.length()));
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


}