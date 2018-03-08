package com.szltech.networklibrary.entifies;

import android.content.Context;

import com.szltech.networklibrary.utils.EncryptUtils;
import com.szltech.networklibrary.utils.GsonUtils;
import com.szltech.networklibrary.utils.PreferenceUtils;

/**
 * Created by Jinphy on 2018/3/8.
 */

public class Cache {

    /**
     * DESC: 缓存时间
     *
     * Created by Jinphy, on 2018/3/8, at 11:01
     */
    public final long time;

    /**
     * DESC: 缓存数据
     * Created by Jinphy, on 2018/3/8, at 11:01
     */
    public final String data;

    private Cache(long time, String data) {
        this.time = time;
        this.data = data;
    }

    /**
     * DESC: 新建缓存
     * Created by Jinphy, on 2018/3/8, at 11:04
     */
    public static void save(Context context, String url, String data) {
        if (context == null) {
            return;
        }
        Cache cache = new Cache(System.currentTimeMillis(), data);
        PreferenceUtils.put(context, PreferenceUtils.File.cache, url, EncryptUtils.aesEncrypt(cache.toString()));
    }

    public static Cache get(Context context, String url) {
        if (context == null) {
            return null;
        }
        String s = PreferenceUtils.get(context, PreferenceUtils.File.cache, url);
        return GsonUtils.toBean(EncryptUtils.aesDecrypt(s), Cache.class);
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }

    /**
     * DESC: 判断缓存是否有效
     * Created by Jinphy, on 2018/3/8, at 11:50
     */
    public static boolean ok(Cache cache, int timeout) {
        long now = System.currentTimeMillis();
        if (cache!=null && now - cache.time < timeout) {
            return GsonUtils.toBean(cache.data, BaseResultEntity.class).ok();
        }
        return false;
    }

}
