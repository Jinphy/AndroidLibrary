package com.szltech.networklibrary.main;

import android.support.annotation.NonNull;

import com.szltech.networklibrary.utils.ObjectUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

/**
 * DESC:一个http请求接口入口类，一个工厂类，提供通用网络请求接口和合并网络请求功能
 *
 * Created by Jinphy on 2017/11/22.
 */

public class HttpUtils {

    // 工具类
    private HttpUtils(){}

    //=============创建 common API实例=================================

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxFragmentActivity activity) {
        return new CommonApi<>(activity, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/11, at 9:19
     */
    public static ApiInterface<String> common(RxAppCompatActivity activity) {
        return new CommonApi<>(activity, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxFragment fragment) {
        return new CommonApi<>(fragment, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxDialogFragment fragment) {
        return new CommonApi<>(fragment, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxAppCompatDialogFragment fragment) {
        return new CommonApi<>(fragment, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxFragmentActivity activity, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(activity, resultClass);
    }


    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxAppCompatActivity activity, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(activity, resultClass);
    }


    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxFragment fragment, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(fragment, resultClass);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxDialogFragment fragment, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(fragment, resultClass);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxAppCompatDialogFragment fragment, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(fragment, resultClass);
    }


    //==========创建 common API实例 ==========================
    public static ApiInterface<Object[]> zipper(RxAppCompatActivity activity){
        return new ZipApi<>(activity,Object[].class);
    }

    public static ApiInterface<Object[]> zipper(RxFragmentActivity activity){
        return new ZipApi<>(activity,Object[].class);
    }
    public static ApiInterface<Object[]> zipper(RxFragment fragment){
        return new ZipApi<>(fragment, Object[].class);
    }
    public static ApiInterface<Object[]> zipper(RxDialogFragment fragment){
        return new ZipApi<>(fragment,Object[].class);
    }
    public static ApiInterface<Object[]> zipper(RxAppCompatDialogFragment fragment){
        return new ZipApi<>(fragment, Object[].class);
    }


}
