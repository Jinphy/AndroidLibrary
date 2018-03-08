package com.szltech.networklibrary.utils;

import com.google.gson.Gson;

/**
 * Created by Jinphy on 2018/3/8.
 */

public class GsonUtils {

    private static final Gson gson = new Gson();


    /**
     * DESC: 把json 转换成JavaBean
     * Created by Jinphy, on 2018/3/8, at 10:10
     */
    public static <T> T toBean(String result, Class<T> clazz) {
        Gson gson = new Gson();
        T t = gson.fromJson(result, clazz);
        return t;
    }

    /**
     * DESC: 把JavaBean转换成json
     * Created by Jinphy, on 2018/3/8, at 10:10
     */
    public static String toJson(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

}
