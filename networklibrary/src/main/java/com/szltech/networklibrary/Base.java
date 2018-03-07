package com.szltech.networklibrary;

import android.content.Context;
import android.support.annotation.CallSuper;

import com.apkfuns.logutils.LogUtils;
import com.dl.dlclient.model.gsmodel.GSGetsyskey;
import com.dl.dlclient.networkApi.HttpService;
import com.dl.dlclient.utils.AnyHelper;
import com.dl.dlclient.utils.AppUtils;
import com.dl.dlclient.utils.StringUtils;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.RetryWhenNetworkException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.HttpConfig;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.converters.StringConverterFactory;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieInterceptor;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.GsonUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * DESC: 包可见类
 * Created by Jinphy, on 2017/12/7, at 20:12
 */
abstract class Base<T> implements ApiInterface<T>, ApiCallback<T> {
    protected static ExecutorService executor = Executors.newCachedThreadPool();

    private static OkHttpClient okHttpClient;

    protected Class<T> resultClass;                // 网络请求返回数据类型对应的类

    //-------------context、activity和fragment---------------------------------------------------------------
    //定义5种是因为Rxlifecycle暂支持5种
    //rx生命周期管理
    protected WeakReference<Context> context;
    protected WeakReference<RxAppCompatActivity> rxAppCompatActivity;
    protected WeakReference<RxFragment> rxFragment;
    protected WeakReference<RxDialogFragment> rxDialogFragment;
    protected WeakReference<RxAppCompatDialogFragment> rxAppCompatDialogFragment;
    protected WeakReference<RxFragmentActivity> rxFragmentActivity;


    //--------------网络请求设置----------------------------------------------------------------------

    protected boolean cancellable;                      // 是否能取消加载框
    protected boolean showProgress;                     // 是否显示加载框
    protected boolean useCache;                         // 是否需要缓存处理
    protected String baseUrl= HttpConfig.baseUrl;       // 基础url
    protected String cachePath;                         // 缓存路径，为对应接口字符串值，设置useCache为true是生效
    protected int connectionTimeout = 10;               // 超时时间，默认十秒
    protected int cookieNetworkTimeout=60;              // 有网情况下的本地缓存时间默认60秒
    protected int cookieNoNetWorkTimeout=24*60*60*30;   // 无网络的情况下本地缓存时间默认30天

    protected Params params;                            // 请求参数
    protected String url = "";                          // 这个url是用来在网络请求错误时打印信息的，网络请求时才会被赋值，赋值地方在Params.doMethod()方法中被
//    protected Map<String,String> decryptParamMap;     // 不加密的参数
    protected boolean hasEncrypted;

    //--------------网络请求回调----------------------------------------------------------------------
    protected Client client;                          // 设置网络请求接口
    protected OnStart onStart;                        // 在onNext调用之前回调
    protected OnResult<T> onResult;                   // 网络请求成功时回调（该回调不判断返回状态码）
    protected OnResultYes<T> onResultYes;             // 网络请求成功并且状态码正确时回调
    protected OnResultNo<T> onResultNo;               // 网络请求成功但是状态码错误时回调
    protected OnCancel onCancel;                      // 网络请求取消时回调
    protected OnError onError;                        // 网络请求错误时回调（例如网络异常）
    protected OnFinally onFinally;                    // 网络请求结束时回调（无论成功或者异常都会回调）


    protected boolean setCookieNoNetworkTimeout;      // 标志是否设置无网时缓存超时
    protected boolean setCookieNetworkTimeout;        // 标志是否设置有网时缓存超时
    protected boolean setConnectionTimeout;           // 标志是否设置连接超时
    protected boolean setBaseUrl;                     // 标志是否设置barsUrl
    protected boolean setUseCache;                    // 标志是否设置使用缓存
    protected boolean useZipperParams = true;         // 标识是否在压缩时使用Zipper的参数，true使用, 默认为true

    protected boolean logParams;                      // 是否打印参数
    protected String logTag = getClass().getSimpleName();


    protected Base(RxAppCompatActivity activity, Class<T> resultClass) {
        setBase();
        this.rxAppCompatActivity = new WeakReference<>(activity);
        this.context = new WeakReference<>(activity);
        this.resultClass = resultClass;
    }

    protected Base(RxFragment fragment, Class<T> resultClass) {
        setBase();
        this.rxFragment = new WeakReference<>(fragment);
        this.context = new WeakReference<>(fragment.getContext());
        this.resultClass = resultClass;
    }

    protected Base( RxDialogFragment fragment, Class<T> resultClass) {
        setBase();
        this.rxDialogFragment = new WeakReference<>(fragment);
        this.context = new WeakReference<>(fragment.getContext());
        this.resultClass = resultClass;
    }

    protected Base( RxAppCompatDialogFragment fragment, Class<T> resultClass) {
        setBase();
        this.rxAppCompatDialogFragment = new WeakReference<>(fragment);
        this.context = new WeakReference<>(fragment.getContext());
        this.resultClass = resultClass;
    }

    protected Base(RxFragmentActivity activity, Class<T> resultClass) {
        setBase();
        this.rxFragmentActivity = new WeakReference<>(activity);
        this.context = new WeakReference<>(activity);
        this.resultClass = resultClass;
    }

    private void setBase() {
        params = Params.newInstance(this);
    }

    /**
     * DESC: 检查参数是否合法
     * Created by Jinphy, on 2017/12/22, at 9:57
     */
    private boolean checkParam(String key, Object value) {
        if (StringUtils.trimEmpty(key) || StringUtils.trimEmpty(value)) {
            return false;
        }
        return true;
    }

    protected final String getUrl() {
        return this.baseUrl + this.cachePath;
    }


    /**
     * DESC: 获取用来加密网络请求参数的SysKey
     * Created by Jinphy, on 2017/12/22, at 13:17
     */
    protected void getSysKey(String accessToken, String idNo, String idType, OnResultYes<String> onNext) {
        if (AnyHelper.reference(context)) {
            Context context = this.context.get();
            // 获取系统加密秘钥
            Api.common((RxFragmentActivity) context, GSGetsyskey.class)
                    .param(Api.Key.ACCESS_TOKEN, accessToken)
                    .param(Api.Key.ID_NO, idNo)
                    .param(Api.Key.ID_TYPE, idType)
                    .showProgress()
                    .cancellable()
                    .client(HttpService::getsyskey)
                    .onResultYes(result -> onNext.call(result.getRetdata().getSyskey()))
                    .execute();
        }
    }


    //------------------回调方法 --------------------------------------------------------------------

    /**
     * DESC: 网络请求开始时回调
     * Created by Jinphy, on 2017/12/28, at 14:19
     */
    @Override
    @CallSuper
    public void doOnStart() {
        if (this.onStart != null) {
            this.onStart.call();
        }
    }

    /**
     * DESC: 网络请求成功时回调，只是网络请求成功，但是返回的数据不一定有效，要看返回状态码和返回信息
     * Created by Jinphy, on 2017/12/22, at 20:34
     */
    @Override
    @CallSuper
    public void doOnResult(T result) {
        if (this.onResult != null) {
            this.onResult.call(result);
        }
        this.doCheckResult(result);
    }

    /**
     * DESC: 网络请求取消时回调
     * Created by Jinphy, on 2017/12/28, at 16:54
     */
    @Override
    @CallSuper
    public void doOnCancel() {
        if (this.onCancel != null) {
            this.onCancel.call();
        }
        if (AppUtils.debug()) {
            LogUtils.e("The request has cancel, the corresponding url = " + url);
        }
    }

    @Override
    @CallSuper
    public void doOnError(Throwable e) {
        if (this.onError != null) {
            this.onError.call(e);
        }
        if (AppUtils.debug()) {
            LogUtils.e("There is an error occurred when request url = " + url+"\n Error message: "+e.getMessage());
        }
    }

    /**
     * DESC: 网络请求结束时回调
     * Created by Jinphy, on 2017/12/26, at 9:38
     */
    @Override
    @CallSuper
    public void doOnFinally() {
        if (this.onFinally != null) {
            this.onFinally.call();
        }
    }

    //-----------------属性设置方法 ------------------------------------------------------------------
    /**
     * DESC: 设置无网时缓存的超时时间
     * Created by Jinphy, on 2017/12/4, at 8:57
     */
    @Override
    public ApiInterface<T> cookieNoNetworkTimeout(int timeout) {
        this.cookieNoNetWorkTimeout = timeout;
        this.setCookieNoNetworkTimeout = true;
        return this;
    }

    /**
     * DESC: 设置有网时缓存的超时时间
     * Created by Jinphy, on 2017/12/4, at 8:57
     */
    @Override
    public ApiInterface<T> cookieNetworkTimeout(int timeout) {
        this.cookieNetworkTimeout = timeout;
        this.setCookieNetworkTimeout = true;
        return this;
    }

    /**
     * DESC: 设置网络连接的超时
     * Created by Jinphy, on 2017/12/4, at 8:58
     */
    @Override
    public ApiInterface<T> connectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
        this.setConnectionTimeout = true;
        return this;
    }


    /**
     * DESC: 设置网络请求的基础URL部分
     * Created by Jinphy, on 2017/12/4, at 9:02
     */
    @Override
    public ApiInterface<T> baseUrl(@Nonnull String baseUrl) {
        AnyHelper.requireNonNull(baseUrl,"baseUrl cannot be null!");
        this.baseUrl = baseUrl;
        this.setBaseUrl = true;
        return this;
    }


    /**
     * DESC: 设置网络请求时使用缓存，默认为不缓存，调用后则缓存，
     *
     *
     * @param cachePath 设置缓存路径，路径值为接口值，例如：{@link HttpConfig.api#addadmire}
     *                  如果设置为空串或者null值则会忽略该设置，所以将不缓存结果
     * Created by Jinphy, on 2017/12/4, at 9:05
     */
    @Override
    public ApiInterface<T> useCache(String cachePath) {
        if (StringUtils.trimEmpty(cachePath)) {
            return this;
        }
        this.cachePath = cachePath;
        this.useCache = true;
        this.setUseCache = true;
        return this;
    }

    /**
     * DESC: 设置在网络请求时显示进度，默认为不显示，调用该方法后则在网络请求时显示进度
     * Created by Jinphy, on 2017/12/4, at 9:07
     */
    @Override
    public ApiInterface<T> showProgress() {
        this.showProgress = true;
        return this;
    }

    /**
     * 设置在压缩多条api请求时是否使用压缩器zipper的参数
     * 默认情况下将使用压缩器设置的参数，如果不需要使用zipper的参数请置false
     *
     * @see ZipApi 和
     * {@link ZipApi#param(String, Object, boolean...)}
     * Created by Jinphy, on 2017/11/29, at 13:50
     */
    @Override
    public ApiInterface<T> useZipperParams(boolean useZipperParams) {
        this.useZipperParams = useZipperParams;
        return this;
    }

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
    @Override
    public ApiInterface<T> param(String key, Object value, boolean... encrypt) {
        if (encrypt.length > 0) {
            params.put(key, value, encrypt[0]);
        } else {
            params.put(key, value);
        }
        return this;
    }

    /**
     * DESC: 设置多个请求参数，参数是安全的，空参数（null 或者空串）将会被自动忽略
     *
     * @param encrypt 可选参数，设置是否加密，默认不传的话是加密的，如果不加密改参数
     *                则设置 {@code encrypt = false }
     *                否则可以不传或者设置{@code encrypt = true} 来加密参数
     *                注意：改参数只接受第一个值，所有多设置的值将会被忽略
     *
     * Created by Jinphy, on 2017/12/21, at 17:38
     */
    @Override
    public ApiInterface<T> params(Map<String, Object> params, boolean... encrypt) {
        if (params == null || params.isEmpty()) {
            return this;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            this.param(entry.getKey(), entry.getValue(),encrypt);
        }
        return this;
    }

    /**
     * DESC: 设置网络请求能够取消，默认不能取消，调用该方法后网络请求允许取消
     * Created by Jinphy, on 2017/12/4, at 9:11
     */
    @Override
    public ApiInterface<T> cancellable() {
        this.cancellable = true;
        return this;
    }


    /**
     * DESC: 设置网络请求接口
     * Created by Jinphy, on 2017/12/4, at 9:16
     */
    @Override
    public ApiInterface<T> client(Client client) {
        this.client = client;
        return this;
    }

    /**
     * DESC: 设置网络请求成功时的的回调
     * Created by Jinphy, on 2017/12/4, at 9:18
     */
    @Override
    public ApiInterface<T> onResult(OnResult<T> listener) {
        this.onResult = listener;
        return this;
    }
//
//    /**
//     * DESC: 设置可观察的网络请求的回调接口
//     * Created by Jinphy, on 2017/12/4, at 9:19
//     */
//    @Override
//    public ApiInterface<T> onObservable(OnObservableNext<T> listener) {
//        this.onObservable = listener;
//        return this;
//    }

    /**
     * DESC: 设置请求成功前的回调
     * Created by Jinphy, on 2017/12/4, at 9:21
     */
    @Override
    public ApiInterface<T> onStart(OnStart listener) {
        this.onStart = listener;
        return this;
    }

    /**
     * DESC: 设置取消回调
     * Created by Jinphy, on 2017/12/4, at 9:22
     */
    @Override
    public ApiInterface<T> onCancel(OnCancel listener) {
        this.onCancel = listener;
        return this;
    }

    /**
     * DESC: 设置网络请求异常回调
     * Created by Jinphy, on 2017/12/4, at 9:22
     */
    @Override
    public ApiInterface<T> onError(OnError listener) {
        this.onError = listener;
        return this;
    }

    /**
     * DESC: 设置网络请求成功单数据错误时的回调
     * Created by Jinphy, on 2017/12/7, at 20:48
     */
    @Override
    public ApiInterface<T> onResultNo(OnResultNo<T> listener) {
        this.onResultNo = listener;
        return this;
    }

    @Override
    public ApiInterface<T> onResultYes(OnResultYes<T> listener) {
        this.onResultYes = listener;
        return this;
    }

    /**
     * DESC: 设置请求结束时回调，无论成功或失败都会回调
     * Created by Jinphy, on 2017/12/26, at 9:38
     */
    @Override
    public ApiInterface<T> onFinally(OnFinally listener) {
        this.onFinally = listener;
        return this;
    }

    //-----------------网络请求的创建-----------------------------------------------------------------

    /**
     * DESC: 设置OkHttpClient
     * Created by Jinphy, on 2017/12/7, at 14:57
     */
    protected OkHttpClient getOkHttpClient() {
        if (AnyHelper.reference(context)) {
            if (okHttpClient == null) {
                synchronized (Base.class) {
                    if (okHttpClient == null) {
                        okHttpClient = new OkHttpClient.Builder().connectTimeout(this.connectionTimeout, TimeUnit.SECONDS).build();
                    }
                }
            }// end if

            return okHttpClient.newBuilder().addInterceptor(new CookieInterceptor(this.useCache, getUrl())).build();
        }// end if

        return null;
    }

    /**
     * DESC: 设置Retrofit
     * Created by Jinphy, on 2017/12/7, at 14:57
     */
    protected Retrofit getRetrofit(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            return null;
        }
        /*创建retrofit对象*/
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(StringConverterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .callFactory(request -> params.doMethod(okHttpClient,request))
                .baseUrl(baseUrl)
                .build();
    }

    private static final String TAG = "Base";

    /**
     * DESC: 获取Observable
     * Created by Jinphy, on 2017/12/7, at 20:43
     */
    @SuppressWarnings("unchecked")
    protected Observable<T> getObservable(Retrofit retrofit) {
        if (retrofit == null) {
            return null;
        }
        AnyHelper.requireNonNull(client, "client 不能为空，该接口在getObservable() 中被调用以获取Observable");

        Observable<String> originObservable = client.call(retrofit.create(HttpService.class), new HashMap<>())
                /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*生命周期管理*/
                // .compose(basePar.getRxAppCompatActivity().bindToLifecycle())
                .compose(bindUntilEvent())
                /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread());


        // 根据指定的resultClass转换请求结果
        Class<T> resultClass = getResultClass();
        AnyHelper.requireNonNull(resultClass, "resultClass cannot be null!");
        if (resultClass == String.class) {
            return originObservable.map(result -> (T) result);
        } else {
            return originObservable.map(result -> GsonUtils.json2Bean(result, resultClass));
        }
    }

    /**
     * DESC: 获取Observable
     * Created by Jinphy, on 2017/12/7, at 20:43
     */
    protected Observable<T> getObservable() {
        if (client == null) {
            return null;
        }
        // 获取 OkHttpClient
        OkHttpClient okHttpClient = getOkHttpClient();

        // 获取 Retrofit
        Retrofit retrofit = getRetrofit(okHttpClient);

        // 获取 Observable
        return getObservable(retrofit);
    }

    /**
     * DESC: 获取网络请求返回数据类型对应的类
     * Created by Jinphy, on 2017/12/7, at 20:46
     */
    protected Class<T> getResultClass() {
        return resultClass;
    }

    /**
     * DESC: 定义5种是因为Rxlifecycle暂支持5种
     * Created by Jinphy, on 2017/12/22, at 13:54
     */
    protected Observable.Transformer  bindUntilEvent(){
        if(AnyHelper.reference(rxAppCompatActivity)){
            return rxAppCompatActivity.get().bindUntilEvent(ActivityEvent.DESTROY);

        }else  if(AnyHelper.reference(rxFragment)){
            return rxFragment.get().bindUntilEvent(FragmentEvent.DESTROY);

        } else if (AnyHelper.reference(rxDialogFragment)) {
            return rxDialogFragment.get().bindUntilEvent(FragmentEvent.DESTROY);

        }else if (AnyHelper.reference(rxAppCompatDialogFragment)) {
            return rxAppCompatDialogFragment.get().bindUntilEvent(FragmentEvent.DESTROY);

        } else if (AnyHelper.reference(rxFragmentActivity)) {
            return rxFragmentActivity.get().bindUntilEvent(ActivityEvent.DESTROY);

        } else {
            return null;
        }
    }
}
