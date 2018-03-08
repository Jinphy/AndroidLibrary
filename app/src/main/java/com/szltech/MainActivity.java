package com.szltech;

import android.Manifest;
import android.os.Bundle;

import com.apkfuns.logutils.LogUtils;
import com.szltech.networklibrary.entifies.BaseResultEntity;
import com.szltech.networklibrary.main.Config;
import com.szltech.networklibrary.main.HttpService;
import com.szltech.networklibrary.main.HttpUtils;
import com.szltech.networklibrary.utils.AppUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        LogUtils.e(AppUtils.getAppVersion(this));

        new RxPermissions(this)
                .request(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    if (granted) {
                        HttpUtils.common(this)
                                .param(Config.Key.fundCode, "23")
                                .param(Config.Key.queryType, 1)
                                .showProgress()
                                .client(HttpService::marketQueryForChart)
                                .onResultYes(LogUtils::e)
                                .onResultNo(LogUtils::e)
                                .onResult(LogUtils::e)
                                .onError(LogUtils::e)
                                .execute();
                    }
                });
    }
}
