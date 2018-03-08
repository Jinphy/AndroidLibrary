package com.szltech.networklibrary.main;

import android.content.Context;
import android.text.TextUtils;

import com.szltech.networklibrary.entifies.Cache;
import com.szltech.networklibrary.utils.EncryptUtils;
import com.szltech.networklibrary.utils.ObjectUtils;

import java.io.IOException;
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
    public static class StringResponseConverter implements Converter<ResponseBody, Object> {

        private final WeakReference<Context> context;
        private final String url;
        private final boolean useCache;

        public StringResponseConverter(WeakReference<Context> context, String url, boolean useCache){
            this.context = context;
            this.url = url;
            this.useCache = useCache;
        }



        @Override
        public Object convert(ResponseBody value) throws IOException {
            String data = EncryptUtils.aesDecrypt(value.string(), EncryptUtils.AES_KEY);
            value.close();
            if (useCache && !TextUtils.isEmpty(url) && ObjectUtils.reference(context)) {
                Cache.save(context.get(), url, data);
            }
            return data;
        }
    }
}