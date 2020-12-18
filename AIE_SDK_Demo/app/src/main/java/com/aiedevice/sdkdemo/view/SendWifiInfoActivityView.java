package com.aiedevice.sdkdemo.view;

public interface SendWifiInfoActivityView extends BaseView {
    void onSendWifiSuccessful(String mainCtl);

    void onSendWifiFailure(int errCode, String errMsg);
}
