package com.szltech.networklibrary;

import android.content.Context;
import android.support.annotation.IntRange;

import com.apkfuns.logutils.LogUtils;
import com.dl.dlclient.model.Account;
import com.dl.dlclient.utils.AnyHelper;
import com.dl.dlclient.utils.AppUtils;
import com.dl.dlclient.utils.NetUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseResultEntity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.GsonUtils;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;


/**
 * DESC: 包可见类
 *      一个合并网络请求接口类，可同时合并多条网络请求接口，结合 {@link CommonApi}使用
 *
 * 使用入口：
 * @see Api#zipper(RxFragment) 或者其重构方法
 * Created by Jinphy, on 2017/12/7, at 20:12
 */
class ZipApi<T> extends Base<T> {

    protected List<CommonApi> apis;

    ZipApi(RxAppCompatActivity rxAppCompatActivity,Class<T> resultClass) {
        super(rxAppCompatActivity,resultClass);
        init();
    }

    ZipApi(RxFragment rxFragment,Class<T> resultClass) {
        super(rxFragment,resultClass);
        init();
    }

    ZipApi(RxDialogFragment rxDialogFragment,Class<T> resultClass) {
        super(rxDialogFragment,resultClass);
        init();
    }

    ZipApi(RxAppCompatDialogFragment rxAppCompatDialogFragment,Class<T> resultClass) {
        super(rxAppCompatDialogFragment,resultClass);
        init();
    }

    ZipApi(RxFragmentActivity rxFragmentActivity,Class<T> resultClass) {
        super(rxFragmentActivity,resultClass);
        init();
    }

    //----------------------------------------------------------------------------------------------
    private void init() {
        apis = new LinkedList<>();
    }

    /**
     * DESC: 检测条件
     * Created by Jinphy, on 2017/12/7, at 18:06
     */
    private void checkCondition() {
        if (this.apis.size() <= 1) {
            throw new RuntimeException("doHttpZip request at lease two BaseApi!");
        }


        for (CommonApi api : apis) {
            if (this.client != null && api.client == null) {
                api.client = this.client;
            }
            if (this.params.size() > 0 && api.useZipperParams) {
                api.params(this.params);
            }
            if (this.setConnectionTimeout && !api.setConnectionTimeout) {
                api.connectionTimeout = this.connectionTimeout;
            }
            if (this.setCookieNetworkTimeout && !api.setCookieNetworkTimeout) {
                api.cookieNetworkTimeout = this.cookieNetworkTimeout;
            }
            if (this.setCookieNoNetworkTimeout && !api.setCookieNoNetworkTimeout) {
                api.cookieNoNetWorkTimeout = this.cookieNoNetWorkTimeout;
            }
            if (this.setBaseUrl && !api.setBaseUrl) {
                api.baseUrl = this.baseUrl;
            }
        }
    }

    /**
     * DESC: 加密参数并执行合并请求
     *
     *
     * @param accessToken 获取sysKey时的传参
     * @param idNo        获取sysKey时的传参，证件号
     * @param idType      获取sysKey时的传参，证件类型
     * @param which 指定需要进行加密请求的API的下标，按照添加的api顺序，第一条网络请求为which = 0，以此类推
     *              注意：不合法的下标将被忽略，所以下标的设置是安全的，另外如果不传该参数则默认加密所有的api
     *
     * Created by Jinphy, on 2017/12/22, at 13:44
     */
    private void encryptAndRequest(String accessToken, String idNo, String idType, int...which) {
        getSysKey(accessToken,idNo,idType,
                sysKey->{
                    if (which.length == 0) {
                        // 不指定具体加密哪条api请求则默认加密所有api
                        for (CommonApi api : apis) {
                            api.params.setSysKey(sysKey);
                        }
                        // 默认加密所有api时，统一设置的参数也要加密
                        this.params.setSysKey(sysKey);
                    } else {
                        // 加密指定的api，这里无需判断是否重复加密，因为encryptParams（）方法以作判断
                        // 当加密指定的api请求时，统一设置的参数不加密
                        for (int i : which) {
                            if (i >= 0 && i < apis.size()) {
                                apis.get(i).params.setSysKey(sysKey);
                            }
                        }
                    }
                    // 加密后再合并执行所有请求
                    this.execute();
                });
    }

    /**
     * DESC: 压缩网络请求
     * Created by Jinphy, on 2017/12/7, at 18:07
     */
    @SuppressWarnings(value = "all")
    private Observable<T> zipObservables(CommonApi[] apis){
        Observable<T> observable = null;
        switch (apis.length) {
            case 2:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        (str1, str2) -> (T)new Object[]{str1, str2});
                break;
            case 3:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        (str1, str2, str3) -> (T)new Object[]{str1, str2, str3});
                break;
            case 4:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        apis[3].getObservable(),
                        (str1, str2, str3, str4) -> (T)new Object[]{str1, str2, str3, str4});
                break;
            case 5:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        apis[3].getObservable(),
                        apis[4].getObservable(),
                        (str1, str2, str3, str4, str5) -> (T)new Object[]{str1, str2, str3, str4, str5});
                break;
            case 6:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        apis[3].getObservable(),
                        apis[4].getObservable(),
                        apis[5].getObservable(),
                        (str1, str2, str3, str4, str5, str6) -> (T)new Object[]{str1, str2, str3, str4, str5, str6});
                break;
            default:
                AnyHelper.throwRuntime("网络请求压缩太多了！");
                break;
        }
        // 返回压缩后的Observable
        return observable;
    }

    /**
     * DESC: 检测请求结果
     *
     * 则判断是否所有的结果都是正确的，即判断每条请求结果的返回码是否正确
     * 如果所有的结果都正确则回调onResultYes
     * 如果有一个请求的结果不正确则回调onResultNo，并且把错误信息放在第一项中
     *
     * DESC: 当网络请求成功时，即调用了doOnResult后调用该方法来解析返回的数据是否正确
     *
     * 1、如果数据正确则会回调 onResultYes 接口
     * 2、如果数据不正确则会回调 onResultNo 接口
     *
     * 注意：当所有的网络请求的返回状态码均正确是，回调 onResultYes
     *          否则只要有一条网络请求的数据是不正确的则回调 onResultNo，同时把这条被检测出来的错误结果
     *          放在结果数组result 中的第一个位置，并且result[0] 是BaseResultEntity类型的对象，
     *          所以在 处理 onResultNo 时，只需判断result[0]（并且把其强制转
     *          换成BaseResultEntity对象来获取错误信息） 即可
     *
     * Created by Jinphy, on 2017/12/28, at 16:39
     */
    @Override
    public void doCheckResult(T result) {
        if (this.onResultYes == null && this.onResultNo == null) {
            return;
        }
        Object[] results = (Object[]) result;
        BaseResultEntity it;
        for (Object item : results) {// 对每一项进行判断
            if (item instanceof BaseResultEntity) {
                it = ((BaseResultEntity) item);
            } else {
                it = GsonUtils.json2Bean(item.toString(), BaseResultEntity.class);
            }
            if (!NetUtils.parseData(it)) {
                results[0] = it;
                if (this.onResultNo != null) {
                    this.onResultNo.call(result);
                }
                return;
            }
        }
        if (this.onResultYes != null) {
            this.onResultYes.call(result);
        }
    }

    @Override
    public void doOnResult(T result) {
        super.doOnResult(result);
        if (AppUtils.debug()) {
            Object[] results = (Object[]) result;
            int i=0;
            for (Object item : results) {
                LogUtils.e(
                        "Request network successful:\n\n" +
                                "【   Url=   】===>>|| "+ apis.get(i).url+",\n\n" +
                                "【 Response 】===>>|| "+item+"\n");
                i++;
            }
        }
    }

    @Override
    public Observable<T> doHttpObservable() {
        return getObservable();
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return null;
    }

    @Override
    public Observable<T> getObservable() {
        return zipObservables(apis.toArray(new CommonApi[apis.size()]));
    }

    /**
     * DESC: 添加网络请求，可以多次调用以设置多条网络请求
     * Created by Jinphy, on 2017/12/4, at 9:24
     */
    @Override
    public <U> ApiInterface<T> api(ApiInterface<U>api ) {
        this.apis.add((CommonApi) api);
        return this;
    }

    //================执行 ==========================================================================


    /**
     * DESC: 合并请求网络
     *
     * Created by Jinphy, on 2017/12/7, at 18:08
     */
    public void execute(){
        executor.execute(()->{
            if (AnyHelper.reference(context)) {
                Context context = this.context.get();
                // 检测条件
                checkCondition();

                // 获取订阅者
                ApiSubscriber<T> subscriber = ApiSubscriber.newInstance(this, context);

                // 获取被观察者
                Observable<T> observable = getObservable();

                // 订阅网络请求
                observable.subscribe(subscriber);
            }
        });

    }

    /**
     * DESC: 合并请求网络，并且使用sysKey加密参数
     *      加密请求的步骤是：
     *          1、首先调用获取systemKey的接口来获取sysKey秘钥
     *          2、根据获取的sysKey加密指定的接口中需要被加密的参数
     *          3、参数加密后再执行所有合并的网络请求

     * @param which 指定需要进行加密请求的API的下标，按照添加的api顺序，第一条网络请求为which = 0，以此类推
     *              注意：不合法的下标将被忽略，所以下标的设置是安全的，另外如果不传该参数则默认加密所有的api.
     *              指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @see Api#common(RxFragment) 及其重载函数
     * @see Api#zipper(RxFragment) 及其重载函数
     * Created by Jinphy, on 2017/12/22, at 13:05
     */
    @Override
    public void executeEncrypted(@IntRange(from = 0) int... which) {
        encryptAndRequest(null, null, null, which);
    }

    /**
     * DESC:
     *
     *      加密请求的步骤是：
     *          1、首先调用获取systemKey的接口来获取sysKey秘钥,这里在获取该秘钥时还要传accessToken
     *          2、根据获取的sysKey加密指定的接口中需要被加密的参数
     *          3、参数加密后再执行所有合并的网络请求
     *
     * @param which 指定需要进行加密请求的API的下标，按照添加的api顺序，第一条网络请求为which = 0，以此类推
     *              注意：不合法的下标将被忽略，所以下标的设置是安全的，另外如果不传该参数则默认加密所有的api.
     *              指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *]
     * @param account 当前账号
     *
     * @see Api#common(RxFragment) 及其重载函数
     * @see Api#zipper(RxFragment) 及其重载函数
     *
     * Created by Jinphy, on 2017/12/22, at 13:07
     */
    @Override
    public void executeEncrypted(Account account, int... which) {
        encryptAndRequest(account.getAccesstoken(),null,null,which);
    }

    /**
     * DESC:
     *
     *      加密请求的步骤是：
     *          1、首先调用获取systemKey的接口来获取sysKey秘钥,这里在获取该秘钥时还要传证件号和证件类型
     *          2、根据获取的sysKey加密指定的接口中需要被加密的参数
     *          3、参数加密后再执行所有合并的网络请求
     *
     * @param which 指定需要进行加密请求的API的下标，按照添加的api顺序，第一条网络请求为which = 0，以此类推
     *              注意：不合法的下标将被忽略，所以下标的设置是安全的，另外如果不传该参数则默认加密所有的api.
     *              指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @param idType 证件类型
     * @param idNo 证件号
     *
     * @see Api#common(RxFragment) 及其重载函数
     * @see Api#zipper(RxFragment) 及其重载函数
     * Created by Jinphy, on 2017/12/22, at 13:07
     */
    @Override
    public void executeEncrypted(String idNo, String idType, int... which) {
        encryptAndRequest(null, idNo, idType, which);
    }

}
