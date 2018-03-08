package com.szltech.networklibrary.entifies;

import android.content.Context;
import android.content.SharedPreferences;

import com.szltech.networklibrary.utils.EncryptUtils;
import com.szltech.networklibrary.utils.GsonUtils;
import com.szltech.networklibrary.utils.PreferenceUtils;

import java.util.Map;

/**
 * 缓存类，如果要限制缓存的大小，那么可以在APP启动时（一般在Application中）
 * 调用{@link Cache#clear(Context, long...)}
 * Created by Jinphy on 2018/3/8.
 */

public class Cache {

    public static final long DEFAULT_TIMEOUT = 24 * 3600_000;// 一天的缓存超时

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


    /**
     * DESC: 清理过期的缓存
     *
     * @param timeouts 可变参数，只取第一个值（不传则默认有效期为一天），表示缓存缓存有效期，默认有效期为一天
     * Created by Jinphy, on 2018/3/8, at 14:43
     */
    public static void clear(Context context, long... timeouts) {
        if (context == null) {
            return;
        }
        long timeout = DEFAULT_TIMEOUT;
        if (timeouts.length > 0) {
            timeout = timeouts[0];
        }
        SharedPreferences.Editor editor = PreferenceUtils.editor(context, PreferenceUtils.File.cache);
        long now = System.currentTimeMillis();
        Map<String, ?> map = PreferenceUtils.getAll(context, PreferenceUtils.File.cache);
        for (Map.Entry<String, ?> x : map.entrySet()) {
            Cache cache = Cache.get(context, x.getKey());
            if (now - cache.time > timeout) {
                editor.remove(x.getKey());
            }
        }
        editor.apply();
    }



}
