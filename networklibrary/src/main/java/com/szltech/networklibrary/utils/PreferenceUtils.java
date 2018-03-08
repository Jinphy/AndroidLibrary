package com.szltech.networklibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.annotation.Nonnull;

/**
 * Created by Jinphy on 2018/3/8.
 */

public class PreferenceUtils {

    public static final String PATH_CACHE = "cache";

    /**
     * DESC: 保存数据
     *
     *
     * @param context 上下文
     * @param file 保存的文件名
     * @param key 保存的键
     * @param value 保存的值
     * Created by Jinphy, on 2018/3/8, at 10:55
     */
    public static void put(@Nonnull Context context, @Nonnull File file, @Nonnull String key, String value) {
        ObjectUtils.requireNonNull(context, "context cannot be null!");
        ObjectUtils.requireNonNull(file, "file cannot be null!");
        ObjectUtils.requireNonNull(key, "key cannot be null!");
        SharedPreferences.Editor edit = context.getSharedPreferences(file.get(), Context.MODE_PRIVATE).edit();
        edit.putString(key, value);
        edit.apply();
    }


    /**
     * DESC: 获取数据
     *
     *
     * @param context 上下文
     * @param file 获取数据的文件名
     * @param key 获取数据的键
     * Created by Jinphy, on 2018/3/8, at 10:56
     */
    public static String get(@Nonnull Context context, @Nonnull File file, @Nonnull String key) {
        ObjectUtils.requireNonNull(context, "context cannot be null!");
        ObjectUtils.requireNonNull(file, "file cannot be null!");
        ObjectUtils.requireNonNull(key, "key cannot be null!");
        return context.getSharedPreferences(file.get(), Context.MODE_PRIVATE).getString(key, "");
    }

    /**
     * DESC: 枚举：指定数据的保存文件路径
     * Created by Jinphy, on 2018/3/8, at 10:57
     */
    public enum File {

        cache("cache");

        private String value;

        File(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }
}
