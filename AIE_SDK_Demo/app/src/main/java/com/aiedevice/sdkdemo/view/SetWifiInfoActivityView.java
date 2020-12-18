package com.aiedevice.sdkdemo.view;

import android.net.wifi.ScanResult;

import java.util.List;

public interface SetWifiInfoActivityView extends BaseView {
    void showWifiList(List<ScanResult> wifiList);

    void showCurrentWifi(String ssid);

    void showOpenWifiDialog();
}
