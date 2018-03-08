package com.szltech.networklibrary.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.lang.ref.SoftReference;

/**
 *
 * Created by Jinphy on 2018/3/7.
 */

public class ToastUtils {


    private static SoftReference<Toast> toast;
    private static final Object lock = new Object();

    public static void showToast(Context context, @NonNull Object msg, boolean isLong){
        Context app = context.getApplicationContext();
        Toast value;
        if (toast == null || toast.get() == null) {
            synchronized (lock){
                if (toast == null || toast.get() == null) {
                    value = Toast.makeText(app, "", Toast.LENGTH_SHORT);
                    toast = new SoftReference<>(value);
                }
            }
        }
        value = toast.get();
        value.setText(msg.toString());
        value.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        value.show();
    }

}
