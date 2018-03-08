package com.szltech.networklibrary.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.szltech.networklibrary.entifies.Cache;
import com.szltech.networklibrary.exceptions.HttpTimeException;
import com.szltech.networklibrary.utils.GsonUtils;
import com.szltech.networklibrary.utils.NetworkUtils;
import com.szltech.networklibrary.utils.ObjectUtils;
import com.szltech.networklibrary.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Observable;
import rx.Subscriber;

/**
 * description: 网络请求回调观察者
 * Created by Jinphy on 2017/11/27.
 */

public class ApiSubscriber<T> extends Subscriber<T> {

    /*请求API*/
    protected BaseApi<T> api;
    /*回调接口*/
    protected ApiCallback<T> apiCallback;
    // 所引用防止内存泄漏
    protected WeakReference<Context> context;
    /*加载框可自己定义*/
    protected Dialog dialog;
    private static final String TAG = "ProgressSubscriber";

    public static <U> ApiSubscriber<U> newInstance(BaseApi<U> api, Context context) {
        return new ApiSubscriber<>(api, context);
    }

    /**
     * 构造
     *
     * @param api
     */
    protected ApiSubscriber(BaseApi<T> api, Context context) {
        this.api = api;
        this.apiCallback = api;
        this.context = new WeakReference<>(context);
        initProgressDialog();
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        if (ObjectUtils.reference(context)) {
            Activity activity = (Activity) context.get();
            activity.runOnUiThread(() -> {
                apiCallback.doOnStart();
                showProgressDialog();
                /*缓存并且有网*/
                if (api.useCache && NetworkUtils.isNetworkAvailable(activity)) {
                    /*获取缓存数据*/
                    loadCache(activity, api.getUrl());
                }
            });
        }
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        Log.e(TAG, "onCompleted: thread = " + Thread.currentThread().getName());
        apiCallback.doOnFinally();
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: thread = " + Thread.currentThread().getName() + e.getMessage());
        Log.e(TAG, "onError: =========================>");

        this.apiCallback.doOnFinally();
        dismissProgressDialog();
        /*需要緩存并且本地有缓存才返回*/
        if (api.useCache) {
            Observable.just(api.getUrl()).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    handleError(e);
                }

                @Override
                public void onNext(String url) {
                    /*获取缓存数据*/
                    if (ObjectUtils.reference(context) && loadCache(context.get(), url)) {
                    } else {
                        throw new HttpTimeException("网络错误!");
                    }
                }
            });
        } else {
            handleError(e);
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param next 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T next) {
        Log.e(TAG, "onNext: thread = " + Thread.currentThread().getName());
        apiCallback.doOnResult(next);

    }

    /**
     * DESC: 统一处理错误
     * Created by Jinphy, on 2018/3/8, at 11:54
     */
    private void handleError(Throwable e) {
        Context context = this.context.get();
        if (context == null) return;
        if (e instanceof SocketTimeoutException
                || e instanceof ConnectException
                || e instanceof UnknownHostException) {
            ToastUtils.showToast(context, "网络中断，请检查您的网络状态!", false);
        } else {
            ToastUtils.showToast(context, "错误" + e.getMessage(), false);
            e.printStackTrace();
        }
        apiCallback.doOnError(e);
    }

    /**
     * DESC: 获取缓存数据，成功则返回true，否则返回false
     * Created by Jinphy, on 2017/12/7, at 15:27
     */
    protected boolean loadCache(Context context, String url) {
        Cache cache = Cache.get(context, url);
        if (Cache.ok(cache, api.cookieNetworkTimeout)) {
            if (api.getResultClass() == String.class) {
                apiCallback.doOnResult((T) cache.data);
            } else {
                apiCallback.doOnResult(GsonUtils.toBean(cache.data, api.getResultClass()));
            }
            onCompleted();
            unsubscribe();
            return true;
        }
        return false;
    }


    /**
     * 初始化加载框
     */
    protected void initProgressDialog() {
        if (!api.showProgress) {
            return;
        }
        if (ObjectUtils.reference(context)) {
            Activity activity = (Activity) context.get();
            activity.runOnUiThread(() -> {
                // TODO: 2018/3/8 在具体的项目中替换项目自己对应的对话框
                dialog = new ProgressDialog(context.get());

                if (api.cancellable) {
                    dialog.setCancelable(true);
                    dialog.setOnCancelListener(dialogInterface -> {
                        apiCallback.doOnCancel();
                        if (!this.isUnsubscribed()) {
                            this.unsubscribe();
                        }
                    });
                }
            });

        }
    }


    /**
     * 显示加载框
     */
    protected void showProgressDialog() {
        if (!api.showProgress
                || dialog == null
                || !ObjectUtils.reference(context)
                || dialog.isShowing()) {
            return;
        }
        dialog.show();
    }


    /**
     * 隐藏
     */
    protected void dismissProgressDialog() {
        if (!api.showProgress) return;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
