package com.szltech.networklibrary;

import android.content.Context;

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

import rx.Observable;

/**
 * DESC: 包可见类
 * Created by Jinphy, on 2017/12/7, at 20:12
 */

/**
 * DESC: 一个通络网络请求接口类，用来执行单条网络请求接口的请求
 *
 * 使用入口：
 * @see Api#common(RxFragment) 或者器重构方法
 * Created by Jinphy, on 2017/12/22, at 12:14
 */
class CommonApi<T> extends Base<T>{

    //=================初始化========================================================================

    /*
     * DESC: 私有化构造函数
     * Created by Jinphy, on 2017/12/4, at 9:37
     */
    CommonApi(RxFragmentActivity activity, Class<T> resultClass) {
        super(activity,resultClass);
        init();
    }

    /*
     * DESC: 私有化构造函数
     * Created by Jinphy, on 2017/12/11, at 9:18
     */
    CommonApi(RxAppCompatActivity activity, Class<T> resultClass) {
        super(activity,resultClass);
        init();
    }

    /*
     * DESC: 私有化构造函数
     * Created by Jinphy, on 2017/12/4, at 9:37
     */
    CommonApi(RxFragment fragment, Class<T> resultClass) {
        super(fragment,resultClass);
        init();
    }

    /*
     * DESC: 私有化构造函数
     * Created by Jinphy, on 2017/12/4, at 9:37
     */
    CommonApi(RxDialogFragment fragment, Class<T> resultClass) {
        super(fragment,resultClass);
        init();
    }

    /*
     * DESC: 私有化构造函数
     * Created by Jinphy, on 2017/12/4, at 9:37
     */
    CommonApi( RxAppCompatDialogFragment fragment, Class<T> resultClass) {
        super(fragment,resultClass);
        init();
    }


    /**
     * DESC: 初始化
     * Created by Jinphy, on 2017/12/4, at 9:37
     */
    protected void init() {
        // no-op
    }

    /**
     * DESC: 检测条件
     * Created by Jinphy, on 2017/12/22, at 11:22
     */
    protected void checkCondition() {
        // no-op
    }

    private static final String TAG = "Api";

    /**
     * DESC: 检测请求结果
     *
     * 则判断请求结果结果是否正确，即判断结果的返回码是否正确
     * 如果结果正确则回调onResultYes
     * 如果不正确则回调onResultNo
     *
     *
     * Created by Jinphy, on 2017/12/28, at 16:39
     */
    @Override
    public void doCheckResult(T result) {
        if (this.onResultYes == null && this.onResultNo == null) {
            return;
        }
        BaseResultEntity it;
        if (result instanceof BaseResultEntity) {
            it = (BaseResultEntity) result;
            // 如果结果是BaseResultEntity类型，则为结果设置url
        } else {
            it = GsonUtils.json2Bean(result.toString(), BaseResultEntity.class);
        }
        if (!NetUtils.parseData(it)) {
            if (this.onResultNo != null) {
                this.onResultNo.call(result);
            }
        } else {
            if (this.onResultYes != null) {
                this.onResultYes.call(result);
            }
        }
    }

    @Override
    public void doOnResult(T result) {
        super.doOnResult(result);
        if (AppUtils.debug()) {
            LogUtils.e(
                    "Request network successful:\n\n" +
                            "【   Url    】===>>|| " + url + ",\n\n" +
                            "【 Response 】===>>|| " + result+"\n");
        }
    }


    @Override
    public <U> ApiInterface<T> api(ApiInterface<U> api) {
        AnyHelper.throwRuntime("common api cannot invoke this method!");
        return this;
    }

    @Override
    public Observable<T> doHttpObservable() {
        // 检测参数是否完整
        checkCondition();
        return getObservable();
    }

    //================执行 ==========================================================================

    //----------------commonApi 的方法 --------------------------------------------------------------
    /**
     * 普通http请求
     *
     * Created by Jinphy ,on 2017/11/24, at 18:06
     */
    @Override
    public void execute() {
        executor.execute(()->{
            if (AnyHelper.reference(context)) {
                Context context = this.context.get();

                // 判断是否满足条件
                this.checkCondition();

                // 获取 Observable
                Observable<T> observable = this.getObservable();
                //
                //        // 回调可观察对象
                //        this.doOnObservable(observable);

                // 创建网络请求订阅者

                ApiSubscriber<T> subscriber = ApiSubscriber.newInstance(this, context);

            /*订阅观察者*/
                observable.subscribe(subscriber);
            }
        });

//        this.context = null; // 不需要这句代码了，因为现在context是弱引用，将会被自动回收
    }

    /**
     * DESC: 加密请求网络
     *      加密请求的步骤是：
     *          1、首先调用获取systemKey的接口来获取sysKey秘钥
     *          2、根据获取的sysKey加密需要被加密的参数
     *          3、参数加密后再执行目的的网络请求接口
     *
     * @param which 指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @see Api#common(RxFragment) 及其重载函数
     * @see Api#zipper(RxFragment) 及其重载函数
     *
     * Created by Jinphy, on 2017/12/4, at 8:53
     */
    @Override
    public void executeEncrypted(int...which) {
        getSysKey(null,null,null,
                sysKey -> {
                    params.setSysKey(sysKey);
                    this.execute();
                });
    }

    /**
     * 登录情况下带密码网络请求
     *      加密请求的步骤是：
     *          1、首先调用获取systemKey的接口来获取sysKey秘钥,这里在获取该秘钥时还要传accessToken
     *          2、根据获取的sysKey加密需要被加密的参数
     *          3、参数加密后再执行目的的网络请求接口
     *
     * @param account 当前账号
     * @param which 指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @see Api#common(RxFragment) 及其重载函数
     * @see Api#zipper(RxFragment) 及其重载函数
     * Created by Jinphy ,on 2017/11/24, at 18:06
     */
    @Override
    public void executeEncrypted(Account account,int...which) {
        if (account != null) {
            getSysKey(account.getAccesstoken(),null,null,
                    sysKey -> {
                        this.params.setSysKey(sysKey);
                        this.execute();
                    });
        }
    }

    /**
     * 没有登录情况下带密码网络请求
     *      加密请求的步骤是：
     *          1、首先调用获取systemKey的接口来获取sysKey秘钥,这里在获取该秘钥时还要传证件号和证件类型
     *          2、根据获取的sysKey加密需要被加密的参数
     *          3、参数加密后再执行目的的网络请求接口
     *
     *
     * @param idType 证件类型
     * @param idNo 证件号
     * @param which 指定需要加密的网络请求API，该参数是针对网络合并请求接口的，单条网络请求的接口将会忽略该参数
     *
     * @see Api#common(RxFragment) 及其重载函数
     * @see Api#zipper(RxFragment) 及其重载函数
     * <p>
     * Created by Jinphy ,on 2017/11/24, at 18:13
     */
    @Override
    public void executeEncrypted(String idNo, String idType, int...which) {
        getSysKey(null,idNo,idType, sysKey -> {
            this.params.setSysKey(sysKey);
            this.execute();
        });
    }

}
