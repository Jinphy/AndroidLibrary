package com.szltech.networklibrary.main;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 类说明：网络请求的接口执行类
 * 1、其中的每个方法对应一个请求接口，使用注解标明接口的请求方法：
 *      1、@GET-->get请求
 *      2、@Post-->post请求
 *      3、@Put-->put请求
 *
 * 2、每个方法对应的接口路径有注解中的参数指定，该参数必须在{@link Config.Path}
 *    中声明
 *
 * 3、每个方法的声明必须与网络请求的接口路径命名一致
 *      例如：网络请求接口的路径命名为：Config.File.queryFundInfo
 *          则，方法的命名为：queryFundInfo
 *
 * Created by Jinphy on 2018/3/7.
 */

public interface HttpService {

    // 例如：
    @GET(Config.Path.Query.queryFundInfo)
    Observable<String> queryFundInfo();


    /**
     * DESC: 获取系统加密秘钥，不同项目可能路径不一样，可以改变{@link
     *      com.szltech.networklibrary.main.Config.Path.System#getSysKey
     *  } 的值
     * Created by Jinphy, on 2018/3/8, at 9:08
     */
    @GET(Config.Path.System.getSysKey)
    Observable<String> getSysKey();


    @GET(Config.Path.Query.marketQueryForChart)
    Observable<String> marketQueryForChart();


}




































