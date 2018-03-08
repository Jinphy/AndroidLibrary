package com.szltech.networklibrary.main;

import java.util.Map;

import rx.Observable;


/**
 * Created by Jinphy on 2017/12/7.
 */

public interface ApiCallback<T> {

    /**
     * DESC: 网络请求开始前回调，可以用来显示刷新控件等
     * Created by Jinphy, on 2017/12/7, at 13:28
     */
    void doOnStart();

    /**
     * DESC: 网络请求成功时回调
     * Created by Jinphy, on 2017/12/7, at 13:25
     */
    void doOnResult(T result);

    /**
     * DESC: 网络请求成功时判断结果数据是否正确时的回调
     * Created by Jinphy, on 2017/12/7, at 19:19
     */
    void doCheckResult(T result);

    /**
     * DESC: 网络请求取消时回调
     * Created by Jinphy, on 2017/12/7, at 13:28
     */
    void doOnCancel();

    /**
     * DESC: 网络请求异常时回调
     * Created by Jinphy, on 2017/12/7, at 13:28
     */
    void doOnError(Throwable e);



    /**
     * DESC: 网络请求结束时回调，无论成功或是失败该方法都会在最后执行
     * Created by Jinphy, on 2017/12/26, at 9:30
     */
    void doOnFinally();


    //=================各个回调接口接口=============================================

    /**
     * DESC: 接口，用以设置网络网络请求的接口函数，每个网络请求必须设置该接口才能执行网络请求，另外为了防止
     * 忘记调用设置该接口，在执行网络请求时如果检测未设置该接口则会直接抛出异常
     * Created by Jinphy, on 2017/12/4, at 9:16
     */
    interface Client{
        Observable<String> call(HttpService service);
    }

    /**
     * DESC: 网络请求成功时的回调接口
     * Created by Jinphy, on 2017/12/4, at 9:17
     */
    interface OnResult<T>{
        void call(T result);
    }
//
//    /**
//     * DESC: 可观察的网络请求的回调接口
//     * Created by Jinphy, on 2017/12/4, at 9:19
//     */
//    interface OnObservableNext<U>{
//        void call(Observable<U> observable);
//    }

    /**
     * DESC: 请求开始是回调
     *
     * Created by Jinphy, on 2017/12/4, at 9:21
     */
    interface OnStart{
        void call();
    }

    /**
     * DESC: 在取消是执行的回调
     * Created by Jinphy, on 2017/12/4, at 9:21
     */
    interface OnCancel{
        void call();
    }

    /**
     * DESC: 网络请求异常回调
     * Created by Jinphy, on 2017/12/4, at 9:22
     */
    interface OnError{
        void call(Throwable throwable);
    }

    /**
     * DESC: 网络请求成功并且数据正确时的回调接口
     * Created by Jinphy, on 2017/12/4, at 9:17
     */
    interface OnResultYes<T>{
        void call(T result);
    }

    /**
     * DESC: 网络请求成功并且数据错误时的回调接口
     * Created by Jinphy, on 2017/12/4, at 9:17
     */
    interface OnResultNo<T>{
        void call(T result);
    }

    /**
     * DESC: 网络请求结束时回调
     *
     *  注：该回调无论网络成功是否成功都会回调，所以可以用来关闭刷新控件等操作
     * Created by Jinphy, on 2017/12/26, at 9:28
     */
    interface OnFinally{
        void call();
    }
}
