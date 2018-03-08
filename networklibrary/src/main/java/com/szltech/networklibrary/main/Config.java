package com.szltech.networklibrary.main;

/**
 * 类说明： 网络请求配置类
 *
 * Created by Jinphy on 2018/3/7.
 */

public interface Config {


    /**
     * DESC: 网络请求接口路径，存放String类型的路径值
     *
     *  注：命名方式为小驼峰命名
     *      例如：有一个接口路径是 "query/queryfundinfo" 的，则变量命名为{@code
     *          String queryFundInfo = "query/queryfundinfo";
     *      }
     * Created by Jinphy, on 2018/3/7, at 15:33
     */
    interface Path{

        // 例如：
        String queryFundInfo = "query/queryfundinfo";

        /**
         * DESC: 获取系统当前加密秘钥，不同项目可能路径不一样，改变该值即可
         * Created by Jinphy, on 2018/3/8, at 9:10
         */
        String getSysKey = "system/getsyskey";

    }
    //----------------------------------------------------------------------------------------------

    /**
     * DESC: 网络请求参数的键，所有网络请求的键都必须在此处定义，不要在别的地方随意定义
     *
     *  注：命名方式为小驼峰命名
     *      例如：有一个参数的键是 "messagecount"，则命名为：{@code
     *          String messageCount = "messagecount";
     *      }
     * Created by Jinphy, on 2018/3/7, at 15:35
     */
    interface Key{

        //例如：
        String messageCount = "messagecount";
    }



    //----------------------------------------------------------------------------------------------
    /**
     * DESC: 网络请求参数的值，该类时定义一些固定的参数值且有代表意义的，例如值为0代表 week
     *
     *  注：命名方式为小驼峰命名
     * Created by Jinphy, on 2018/3/7, at 15:36
     */
    interface Value{

        // 例如：
        String week = "0";
    }

}
