package com.szltech.networklibrary.utils;

import java.lang.ref.Reference;

/**
 *
 * Created by Jinphy on 2018/3/7.
 */

public class ObjectUtils {


    /**
     * DESC: 判断引用是否存在
     * Created by Jinphy, on 2017/12/22, at 10:35
     */
    public static boolean reference(Reference value){
        return value != null && value.get() != null;
    }


    /**
     * DESC: 非空检测
     * Created by Jinphy, on 2017/12/11, at 18:20
     */
    public static void requireNonNull(Object object, String msg) {
        // 空指针或者空数组
        if (object == null || (object instanceof Object[] &&((Object[]) object).length == 0)) {
            ObjectUtils.throwNull(msg);
        }
    }

    /**
     * DESC: 抛出空指针异常
     * Created by Jinphy, on 2017/12/7, at 10:57
     */
    public static void throwNull(String msg) {
        throw new NullPointerException(msg);
    }

    /**
     * DESC: 抛出运行时异常
     * Created by Jinphy, on 2017/12/7, at 16:22
     */
    public static void throwRuntime(String msg) {
        throw new RuntimeException(msg);
    }


}
