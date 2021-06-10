package com.aiedevice.sdkdemo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.aiedevice.sdk.StpSDK;
import com.aiedevice.sdk.base.SDKConfig;
import com.igexin.sdk.PushManager;

public class MyApplication extends MultiDexApplication {
    /**
     * 正式接入时需要替换AIE分配的packageId
     */
    public static final String PACKAGE_ID = "aie.app";

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 初始化StpSDK
         */
        StpSDK.getInstance().init(this, PACKAGE_ID, SDKConfig.ENV_TEST);

        /**2
         * 初始化个推服务
         */
        PushManager.getInstance().initialize(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);  //这行代码一定要加
        MultiDex.install(this);
    }

}
