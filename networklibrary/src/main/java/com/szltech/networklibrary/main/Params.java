package com.szltech.networklibrary.main;

import android.content.Context;
import android.text.TextUtils;

import com.szltech.networklibrary.utils.AppUtils;
import com.szltech.networklibrary.utils.EncryptUtils;
import com.szltech.networklibrary.utils.ObjectUtils;
import com.szltech.networklibrary.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * DESC：网络请求参数类，包可见
 *
 *      使用说明：该类时网络请求参数封装类，用来操作请求参数
 *      1、参数设置是安全的，你可以设置任何类型的参数，例如int、float、等，同时参数设置是安全的
 *          你可以设置空值，此时该设置将被忽略而不会影响网络请求的正确性，这对非必传的参数是非常有用的

 *      2、公共参数将会在创建该对象的时候被自动添加
 *
 *      3、该类会在内部自动执行编码、签名、和AES加密，你要做的就只是设置你要传的参数（除了公共参数）
 *
 *      4、最终调用{@link Params#doMethod(OkHttpClient, Request)} 方法来或者一个OkHttp的Call
 *
 * Created by Jinphy on 2017/12/25.
 */

class Params extends HashMap<String, String> {

    // AES 加密秘钥
    public static final String AES_KEY = "OhgkU9HlPbmmXvFpZd2zStk8HfVNHMd4cAbtuNwrpeyUyCMyNFuDHXgiAYBKgcQZNJUatazKWp7eiE4mdmqccQ9ourHF6Hz0WrjrXnbdQxDQ3JCC0i7kgXH6wwWLdSv0";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";

    //-------公共参数--------------------------------------------------------------------------------
    public static final String KEY_APP_KEY = "appkey";
    public static final String KEY_APP_SECRET = "appsecret";
    public static final String KEY_APP_VERSION = "appversion";
    public static final String KEY_MARKET = "market";
    public static final String KEY_CHANNEL = "channel";


    public static final String VALUE_APP_KEY = "IX8pBy";
    public static final String VALUE_APP_SECRET = "u9KMNq";
    public static String VALUE_APP_VERSION;
    public static final String VALUE_MARKET = "g1";
    public static final String VALUE_CHANNEL = "app_android";
    //-------签名参数--------------------------------------------------------------------------------
    public static final String KEY_SIGN = "sign";

    //-------其他参数--------------------------------------------------------------------------------
    public static final String KEY_IMG_BASE_64 = "imgbase64";
    public static final String KEY_CONTENT = "content";

    /**
     * DESC: 判断当前参数集是否已经经过SysKey加密过
     * Created by Jinphy, on 2017/12/26, at 11:45
     */
    private boolean hasEncryptedWithSysKey = false;

    private String sysKey;

    private WeakReference<BaseApi> api;

//
//    private List<String> signs = new LinkedList<>();

    /**
     * DESC: 需要跳过AES加密的参数集，所有网络请求参数默认会进行AES加密
     *      但是在这个集合里的参数将不会被AES加密，如果一个参数不需要加密
     *      则要调用{@link ApiInterface#param(String, Object, boolean...)},并设置第三个参数为false
     *
     * Created by Jinphy, on 2017/12/25, at 17:36
     */
    private Set<String> skipAESEncryptSet = new HashSet<String>(8){
        {
            add(KEY_APP_KEY);
            add(KEY_APP_SECRET);
            add(KEY_APP_VERSION);
            add(KEY_MARKET);
            add(KEY_CHANNEL);
            add(KEY_SIGN);
            add(KEY_IMG_BASE_64);
            add(KEY_CONTENT);
        }
    };

    /**
     * DESC: 需要进行SysKey秘钥加密的参数集
     *
     * Created by Jinphy, on 2017/12/26, at 11:42
     */
    private static final Set<String> encryptSysKeySet = new HashSet<String>(){{
        add("pw");
        add("password");
        add("tradepassword");
        add("querybank");
        add("newpwd");
        add("oldpwd");
        add("password");
        add("newpasswd");
        add("tpasswd");
    }};

    private static final String TAG = "Params";
    //----------------------------------------------------------------------------------------------

    /**
     * DESC: 获取一个请求参数的实例
     * Created by Jinphy, on 2017/12/25, at 17:06
     */
    public static Params newInstance(BaseApi api) {
        return new Params(api);
    }

    /**
     * DESC: 创建请求参数，并添加公共参数
     * Created by Jinphy, on 2017/12/25, at 18:37
     */
    private Params(BaseApi api){
        this.api = new WeakReference<>(api);

        if (ObjectUtils.reference(api.context)) {
            VALUE_APP_VERSION = AppUtils.getAppVersion((Context) api.context.get());
        } else {
            VALUE_APP_VERSION = "3.0.2";
        }

        put(KEY_APP_KEY, VALUE_APP_KEY);
        put(KEY_APP_SECRET, VALUE_APP_SECRET);
        put(KEY_APP_VERSION, VALUE_APP_VERSION);
        put(KEY_MARKET, VALUE_MARKET);
        put(KEY_CHANNEL, VALUE_CHANNEL);
    }

    /**
     * DESC: 添加参数
     *
     * @param encrypt 是否加密该参数
     *
     * Created by Jinphy, on 2017/12/25, at 20:25
     */
    public String put(String key, Object value, boolean encrypt) {

        // 判断参数是否合法，合法则添加，否则忽略
        if (check(key, value)) {
            if (!encrypt) {
                skipAESEncryptSet.add(key);
            }
            super.put(key, value.toString());
        }
        return null;
    }

    /**
     * DESC: 添加参数
     *
     *
     * Created by Jinphy, on 2017/12/25, at 20:25
     */
    @Override
    public String put(String key, String value) {
        if (check(key, value)) {
            return super.put(key, value);
        }
        return null;
    }

    /**
     * DESC: 添加参数
     *
     *
     * Created by Jinphy, on 2017/12/25, at 20:25
     */
    public String put(String key, Object value) {
        if (check(key, value)) {
            return super.put(key, value.toString());
        }
        return null;
    }

    /**
     * DESC: 调用该方法设置网络请求参数，并获取call对象，
     *
     * @see retrofit2.Retrofit.Builder#callFactory(Call.Factory)
     *
     * Created by Jinphy, on 2017/12/26, at 11:46
     */
    public Call doMethod(OkHttpClient client,Request request) {
        Call call;
        switch (request.method().toUpperCase()) {
            case GET:
                call = doGet(client, request);
                break;
            case POST:
                call = doPost(client, request);
                break;
            case PUT:
                call = doPut(client, request);
                break;
            default:
                call = doGet(client, request);
                break;
        }
        if (ObjectUtils.reference(this.api)) {
            this.api.get().url = request.url().toString()+"?"+toString();
        }

        return call;
    }

    /**
     * DESC: 内部调用方法，执行get方法请求的参数设置
     * Created by Jinphy, on 2017/12/25, at 19:45
     */
    private Call doGet(OkHttpClient client, Request request) {
        // 加密参数
        encrypt();

        HttpUrl url = request.url();
        HttpUrl.Builder builder = url.newBuilder();

        //添加请求参数
        for (Entry<String, String> param : entrySet()) {
            builder.setQueryParameter(param.getKey(), param.getValue());
        }

        // 创建call 对象
        return client.newCall(request.newBuilder().url(builder.build()).build());
    }

    /**
     * DESC: 内部调用方法，执行post方法请求的参数设置
     * Created by Jinphy, on 2017/12/25, at 19:45
     */
    private Call doPost(OkHttpClient client, Request request) {
        // 加密参数
        encrypt();

        // 创建请求body
        FormBody.Builder builder = new FormBody.Builder();
        for (Entry<String, String> param : entrySet()) {
            builder.addEncoded(param.getKey(), param.getValue());
        }

        // 创建call 对象
        return client.newCall(request.newBuilder().post(builder.build()).build());
    }

    /**
     * DESC: 内部调用方法，执行put方法请求的参数设置
     * Created by Jinphy, on 2017/12/25, at 19:45
     */
    private Call doPut(OkHttpClient client, Request request) {
        // 加密参数
        encrypt();

        // 创建请求body
        FormBody.Builder builder = new FormBody.Builder();
        for (Entry<String, String> param : entrySet()) {
            builder.addEncoded(param.getKey(), param.getValue());
        }

        // 创建call 对象
        return client.newCall(request.newBuilder().put(builder.build()).build());
    }

    /**
     * DESC: 使用AES加密参数
     * Created by Jinphy, on 2017/12/25, at 20:58
     */
    public final void encrypt() {
        // 加密前先获取sign参数
        sign();

        // 加密
        for (Entry<String, String> entry : entrySet()) {
            if (needEncryptAES(entry.getKey())) {
                entry.setValue(EncryptUtils.aesEncrypt(entry.getValue()));
            }
        }
    }

    @Override
    public String toString() {
        int size = size();
        if (size == 0) {
            return "";
        }
        String[] params = new String[size];
        int i=0;
        for (Entry<String, String> param : entrySet()) {
            params[i++] = param.getKey() + "=" + param.getValue();
        }
        return TextUtils.join("&", params);
    }

    /**
     * DESC: 签名参数，并返回参数map
     * Created by Jinphy, on 2017/12/25, at 17:53
     */
    private Params sign() {
        // 获取签名参数前先编码
        encode();

        // 编码
        String[] keyValues = new String[size()];
        int i = 0;
        for (Entry<String, String> entry : entrySet()) {
            keyValues[i++] = entry.getKey() + "=" + entry.getValue();
        }
        Arrays.sort(keyValues);

        String sign = TextUtils.join("&", keyValues);

        put(KEY_SIGN, encryptSign(sign));
        return this;
    }

    /**
     * DESC: 编码参数
     *
     * Created by Jinphy, on 2017/12/25, at 20:40
     */
    private void encode() {
        // 编码前先进行sysKey加密
        encryptWithSysKey();

        for (Entry<String, String> entry : entrySet()) {
            entry.setValue(URLEncoder.encode(entry.getValue()));
        }
    }

    /**
     * DESC: 对请求参数进行sysKey加密
     *
     * Created by Jinphy, on 2017/12/27, at 9:17
     */
    private final void encryptWithSysKey() {
        if (hasEncryptedWithSysKey || TextUtils.isEmpty(sysKey)) {
            return;
        }

        hasEncryptedWithSysKey = true;

        for (Map.Entry<String, String> entry : entrySet()) {
            // 判断key对应的value是否需要加密
            if (needEncryptSysKey(entry.getKey())) {
                entry.setValue(EncryptUtils.aesEncrypt(entry.getValue(), sysKey));
            }
        }
    }

    /**
     * DESC: 用MD5字符串进行加密，这里用来加密sign
     *
     * Created by Jinphy, on 2017/12/25, at 18:45
     */
    private String encryptSign(String signs) {
        String md5Str = EncryptUtils.md5Encrypt(signs);
        // 要生成的sign的字符
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h",
                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
                "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z"};

        // 把加密字符按照8位一组 16进制与0x3FFFFFFF 进行位与运算
        // 这里需要使用long 型来转换，因为 Integer.pareInt()只能处理 31位，首位为符号位，如果不用long，则会越界
        long offset = 0x3FFFFFFF & Long.parseLong(md5Str.substring(0, 8), 16);
        StringBuilder out = new StringBuilder();

        // 循环获得每组6位的字符串
        for (int j = 0; j < 6; j++) {
            // 把得到的值于0x0000003D进行位与运算，取得字符数组chars索引
            // (具体需要看chars数组的长度 以防下标溢出，注意起点为0)
            int index = (int) (0x0000003D & offset);

            // 把取得的字符相加
            out.append(chars[index]);

            // 每次循环按位右移5位
            offset >>= 5;
        }
        return out.toString();
    }

    /**
     * DESC: 判断请求参数是否合法
     * Created by Jinphy, on 2017/12/25, at 17:49
     */
    public final boolean check(String key, Object value) {
        if (StringUtils.trimEmpty(key) || StringUtils.trimEmpty(value)) {
            return false;
        }
        return true;
    }

    /**
     * DESC: 设置用来加密参数的sysKey
     *
     * Created by Jinphy, on 2017/12/22, at 11:06
     */
    public final void setSysKey(String sysKey) {
        ObjectUtils.requireNonNull(sysKey, "sysKey cannot be null!");
        this.sysKey = sysKey;
    }


    /**
     * DESC: 判断参数是否需要进行AES加密
     *
     * @param key 参数名
     * Created by Jinphy, on 2017/12/25, at 17:37
     */
    public final boolean needEncryptAES(String key) {
        return !skipAESEncryptSet.contains(key);
    }

    /**
     * DESC: 判断是否需要进行系统秘钥sysKey加密
     *
     * @param key 参数名
     * Created by Jinphy, on 2017/12/26, at 8:50
     */
    public final boolean needEncryptSysKey(String key) {
        return encryptSysKeySet.contains(key);
    }

}
