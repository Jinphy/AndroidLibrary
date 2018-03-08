package com.szltech.networklibrary.utils;

import android.content.Context;

/**
 * Created by Jinphy on 2018/3/8.
 */

public class AppUtils {


    /**
     * DESC: 获取当前APP的版本， 例如：3.0.3
     * Created by Jinphy, on 2017/12/25, at 17:26
     */
    public static String getAppVersion(Context context) {
        if (context != null) {
            try {
                return context
                        .getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0)
                        .versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
