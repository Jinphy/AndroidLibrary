package com.szltech.networklibrary; /**
 * 包说明：
 *      此包下的所有类文件都与网络请求有关，在平时请求网络时，一般中用到{@link com.dl.dlclient.api.Api} 类，
 * <p>{@code
 *     示例：
 *         1、单条网络请求：
 *          Api.common(activity , GSAccount.class)
 *               .param(Api.Key.$key$,$value$ )
 *               .showProgress($progress$ )
 *               .cancellable()
 *               .useCache()
 *               .cachePath(HttpConfig.api.fundlist)
 *               .onResult(result -> {})
 *               .onResultYes(yesResult->{})
 *               .onResultNo(noResult->{})
 *               .onError(e->{})
 *               .client(HttpService::$client$ )
 *               .onResult(result -> {
 *               })
 *               .execute();
 *
 *
 *           2、多条网络一起请求:
 *           Api.zipper(this)
 *               .api($api1$ )
 *               .api($api2$ )
 *               .param(CommonApi.Key.$key$, $value$ )
 *               .showProgress($progress$ )
 *               .client(HttpService::$client$ )
 *               .onResult(results->{
 *               })
 *               .doHttpZip();
 *
 * }</p>
 *     <h1>类说明：</h1>
 *     <p>
 *          1. {@link com.dl.dlclient.api.Api}:
 *                  该类时暴露给用户调用的接口，用该来执行网络请求的代码入口，是一个工厂类
 *
 *          2. {@link com.dl.dlclient.api.CommonApi}:
 *                  该类时一个包可见类，不暴露给调用用户，用户调用{@link com.dl.dlclient.api.Api#common(com.trello.rxlifecycle.components.support.RxFragment)}
 *                  方法或者其重载方法可以获得该实例的实现，但是返回的时{@link com.dl.dlclient.api.ApiInterface}对象，因为{@code CommonApi}是实现了该接口的
 *
 *          3. {@link com.dl.dlclient.api.ZipApi}:
 *                  该类也是一个包可见类，不暴露给用户，与{@code CommonApi}类似，用户可以调用{@link com.dl.dlclient.api.Api#zipper(com.trello.rxlifecycle.components.support.RxFragment)}
 *                  方法或者其重载方法来获取该实例的实现，同样，返回的是{@code ApiInterface}对象，该类同样实现了{@code ApiInterface}接口
 *
 *          4. {@link com.dl.dlclient.api.Base}:
 *                  该类是{@code CommonApi} 和 {@code ZipApi} 的基类，实现类大部分网络请求的设置，是个抽象类
 *
 *          5.{@link com.dl.dlclient.api.ApiInterface}:
 *                  网络请求接口，用来设置网络请求的配置、参数，以及请求网络，网络请求要实现该接口，例如{@code CommonApi} 和 {@code ZipApi}都实现了该接口，
 *                  然后由网络请求工厂类{@code Api} 来产生相应功能的api
 *
 *          6.{@link com.dl.dlclient.api.ApiCallback}:
 *                  网络请求回到接口，该接口管理各种回调，每种不同功能的api类都要实现该接口以执行回调功能
 *
 *          7. {@link com.dl.dlclient.api.ApiSubscriber}:
 *                  网络请求订阅者，负责协调网络请求过程中的不同阶段的事件，由该订阅者执行各种回调等事件
 *      </p>
 *
 * */