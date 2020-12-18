package com.aiedevice.sdkdemo.view;

import android.bluetooth.BluetoothDevice;

import com.aiedevice.sdkdemo.bean.EspBleDevice;

public interface SearchDeviceActivityView extends BaseView {
    void showSearching();

    void showNoDevice();

    void onFindDevice(EspBleDevice device);

    void gotoSetWifiInfoActivity(BluetoothDevice device);
}
