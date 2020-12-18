package com.aiedevice.sdkdemo.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by code on 17-11-3.
 */
public class EspBleDevice {
    public BluetoothDevice device;
    public boolean checked = false;
    public int rssi = 1;

    public EspBleDevice(BluetoothDevice d) {
        device = d;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EspBleDevice)) {
            return false;
        }

        return device.equals(((EspBleDevice) obj).device);
    }

    @Override
    public int hashCode() {
        int result = device != null ? device.hashCode() : 0;
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + rssi;
        return result;
    }
}
