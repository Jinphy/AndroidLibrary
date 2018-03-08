package com.szltech.networklibrary.main;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.Map;

import rx.Observable;

/**
 * API请求接口
 * Created by Jinphy on 2017/11/24.
 */

public interface ApiInterface<U> {

    /**
     * DESC: 设置cookie没有连接的超时
     * Created by Jinphy, on 2017/12/4, at 8:57
     */
    ApiInterface<U> cookieNoNetworkTimeout(int timeout);

    /**
     * DESC: 设置cookie连接的超时
     * Created by Jinphy, on 2017/12/4, at 8:57
     */
    ApiInterface<U> cookieNetworkTimeout(int timeout);

    /**
     * DESC: 设置网络连接的超时
     * Created by Jinphy, on 2017/12/4, at 8:58
     */
    ApiInterface<U> connectionTimeout(int timeout);

    /**
     * DESC: 设置网络请求的基础URL部分
     * Created by Jinphy, on 2017/12/4, at 9:02
     */
    ApiInterface<U> baseUrl(String baseUrl);

    /**
     * DESC: 设置网络请求时使用缓存，默认为不缓存，调用后则缓存，同时要调用{@code cachePath()}方法设置相应的路径
     *       调用该方法可以对指定的API接口{@code cachePath }进行缓存
     * @param cachePath 设置指定的API接口路径
     * @see Config
     * Created by Jinphy, on 2017/12/4, at 9:05
     */
    ApiInterface<U> useCache(String cachePath);

    /**
     * DESC: 设置在网络请求时显示进度，默认为不显示，调用该方法后则在网络请求时显示进度
     * Created by Jinphy, on 2017/12/4, at 9:07
     */
    ApiInterface<U> showProgress();

    /**
     * DESC: 添加网络请求时的请求参数，该方法可以多次调用来设置多个参数
     *
     *
     * @param encrypt 可选参数，设置是否加密，默认不传的话是加密的，如果不加密改参数
     *                则设置 {@code encrypt = false }
     *                否则可以不传或者设置{@code encrypt = true} 来加密参数
     *                注意：改参数只接受第一个值，所有多设置的值将会被忽略
     *
     * Created by Jinphy, on 2017/12/4, at 9:08
     */
    ApiInterface<U> param(String key, Object value, boolean... encrypt);


    /**
     * DESC: 设置多个请求参数，参数是安全的，空参数（null 或者空串）将会被自动忽略
     *
     *
     * @param encrypt 可选参数，设置是否加密，默认不传的话是加密的，如果不加密改参数
     *                则设置 {@code encrypt = false }
     *                否则可以不传或者设置{@code encrypt = true} 来加密参数
     *                注意：改参数只接受第一个值，所有多设置的值将会被忽略
     *
     * Created by Jinphy, on 2017/12/21, at 17:38
     */
    ApiInterface<U> params(Map<String, Object> params, boolean... encrypt);


    /**
     * DESC: 当调用合并请求时，调用该函数来设置某条网络请求，可以多次调用来合并多条网络请求
     * Created by Jinphy, on 2017/12/26, at 10:35
     */
    <T> ApiInterface<U> api(ApiInterface<T> api);

    /**
     * DESC: 设置网络请求能够取消，默认不能取消，调用该方法后网络请求允许取消
     * Created by Jinphy, on 2017/12/4, at 9:11
     */
    ApiInterface<U> cancellable();

    /**
     * 设置在压缩多条api请求时是否使用压缩器zipper的参数
     * 默认情况下将使用压缩器设置的参数，如果不需要使用zipper的参数请置false
     *
     * @see ZipApi 和
     * {@link ZipApi#param(String, Object, boolean...)}
     * Created by Jinphy, on 2017/11/29, at 13:50
     */
    ApiInterface<U> useZipperParams(boolean useZipperParams);

    /**
     * DESC: 设置网络请求接口
     * Created by Jinphy, on 2017/12/4, at 9:16
     */
    ApiInterface<U> client(ApiCallback.Client client);

    /**
     * DESC: 设置请求开始时回调
     * Created by Jinphy, on 2017/12/4, at 9:21
     */
    ApiInterface<U> onStart(ApiCallback.OnStart listener);


    /**
     * DESC: 设置网络请求成功时的的回调
     * Created by Jinphy, on 2017/12/4, at 9:18
     */
    ApiInterface<U> onResult(ApiCallback.OnResult<U> listener);

    /**
     * DESC: 设置网络请求成功并且数据正确时的回调
     * Created by Jinphy, on 2017/12/7, at 19:23
     */
    ApiInterface<U> onResultYes(ApiCallback.OnResultYes<U> listener);

    /**
     * DESC: 设置网络请求成功但是数据错误时的回调
     * Created by Jinphy, on 2017/12/7, at 19:23
     */
    ApiInterface<U> onResultNo(ApiCallback.OnResultNo<U> listener);

    /**
     * DESC: 设置取消回调
     * Created by Jinphy, on 2017/12/4, at 9:22
     */
    ApiInterface<U> onCancel(ApiCallback.OnCancel listener);

    /**
     * DESC: 设置网络请求异常回调
     * Created by Jinphy, on 2017/12/4, at 9:22
     */
    ApiInterface<U> onError(ApiCallback.OnError listener);

    /**
     * DESC: 设置网络请求结束时回调
     * Created by Jinphy, on 2017/12/26, at 9:34
     */
    ApiInterface<U> onFinally(ApiCallback.OnFinally listener);

    //-----------执行方法 ---------------------------------------------------------------------------

    /**
     * DESC: 执行网络请求
     *
     * Created by Jinphy, on 2017/12/25, at 10:49
     */
    void execute();

    /**
     * DESC: 执行网络请求
     *
     * @param which 指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @see HttpUtils#common(RxFragment) 及其重载函数
     * @see HttpUtils#zipper(RxFragment) 及其重载函数
     *
     * Created by Jinphy, on 2017/12/25, at 10:49
     */
    void executeEncrypted(int... which);


    /**
     * DESC: 执行网络请求
     *
     *
     * @param accessToken 当前账号的accessToken
     * @param which 指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @see HttpUtils#common(RxFragment) 及其重载函数
     * @see HttpUtils#zipper(RxFragment) 及其重载函数
     *
     * Created by Jinphy, on 2017/12/25, at 10:49
     */
    void executeEncrypted(String accessToken, int... which);


    /**
     * DESC: 执行网络请求
     *
     * @param idType 证件类型
     * @param idNo 证件号
     * @param which 指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @see HttpUtils#common(RxFragment) 及其重载函数
     * @see HttpUtils#zipper(RxFragment) 及其重载函数
     *
     * Created by Jinphy, on 2017/12/25, at 10:49
     */
    void executeEncrypted(String idNo, String idType, int... which);


    Observable<U> doHttpObservable();
}
