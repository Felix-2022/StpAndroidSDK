package com.aiedevice.sdkdemo;

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
        StpSDK.getInstance().init(this, PACKAGE_ID, SDKConfig.ENV_ONLINE);
        /**2
         * 初始化个推服务
         */
        PushManager.getInstance().initialize(this);

    }


}
