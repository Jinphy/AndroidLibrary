package com.szltech.networklibrary.entifies;

/**
 * 网络请求返回结果的实体基类
 * Created by Jinphy on 2018/3/7.
 */

public class BaseResultEntity {

    public static final String OK_CODE = "0000";

    // 请求接口
    public String url;
    //  判断标示
    public String retcode;
    //    提示信息
    public String retmsg;
    //显示数据（用户需要关心的数据）



    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    @Override
    public String toString() {
        return "BaseResultEntity{" +
                "retcode='" + retcode + '\'' +
                ", retmsg='" + retmsg + '\'' +
                '}';
    }

    /**
     * DESC: 设置请求接口
     * Created by Jinphy, on 2017/12/28, at 15:15
     */
    public void url(String url) {
        this.url = url;
    }

    /**
     * DESC: 获取url
     * Created by Jinphy, on 2017/12/28, at 15:16
     */
    public String url() {
        return url;
    }

    public boolean ok() {
        return OK_CODE.equals(retcode);
    }
}
