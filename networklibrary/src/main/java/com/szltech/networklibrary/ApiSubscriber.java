package com.szltech.networklibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.dl.dlclient.utils.AnyHelper;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.HttpTimeException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.LibNetUtils;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.ParamsUtls;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.converters.HloveyRC4;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulte;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.thirdpart.CustomProgressDialog;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.CookieDbUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.GsonUtils;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.ToastUtils;

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
    protected Base<T> api;
    /*回调接口*/
    protected ApiCallback<T> apiCallback;
    // 所引用防止内存泄漏
    protected WeakReference<Context> context;
    /*加载框可自己定义*/
    protected Dialog dialog;
    private static final String TAG = "ProgressSubscriber";

    public static <U> ApiSubscriber<U> newInstance(Base<U> api, Context context) {
        return new ApiSubscriber<>(api, context);
    }

    /**
     * 构造
     *
     * @param api
     */
    protected ApiSubscriber(Base<T> api, Context context) {
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
        if (AnyHelper.reference(context)) {
            ((Activity) context.get()).runOnUiThread(() -> {
                apiCallback.doOnStart();
                showProgressDialog();
                /*缓存并且有网*/
                if (api.useCache && AppUtil.isNetworkAvailable(RxRetrofitApp.getApplication())) {
                    /*获取缓存数据*/
                    loadCache(api.getUrl());
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
                    errorDo(e);
                }

                @Override
                public void onNext(String url) {
                    /*获取缓存数据*/
                    if (!loadCache(url)) {
                        throw new HttpTimeException("网络错误!");
                    }
                }
            });
        } else {
            errorDo(e);
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

    /*错误统一处理*/
    private void errorDo(Throwable e) {
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
    protected boolean loadCache(String url) {
        CookieResulte cookieResulte = new CookieDbUtil().queryCookieBy(url);
        if (cookieResulte != null) {
            if (verifyCookieData(cookieResulte.getResulte())) {//成功了（即状态码为成功时）才取缓存没有成功不取缓存
                long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                if (time < api.cookieNetworkTimeout) {
                    String cache = HloveyRC4.decrypt(cookieResulte.getResulte(), ParamsUtls
                            .getAESKEY());
                    if (api.getResultClass() == String.class) {
                        apiCallback.doOnResult((T) cache);
                    } else {
                        apiCallback.doOnResult(GsonUtils.json2Bean(cache, api.getResultClass()));
                    }
                    onCompleted();
                    unsubscribe();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verifyCookieData(String data) {
        try {
            return LibNetUtils.parseData(HloveyRC4.decrypt(data, ParamsUtls.getAESKEY()));
        } catch (Exception e) {
            e.printStackTrace();
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
        if (AnyHelper.reference(context)) {
            Activity activity = (Activity) context.get();
            activity.runOnUiThread(() -> {
                dialog = new CustomProgressDialog(context.get());
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
                || !AnyHelper.reference(context)
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
