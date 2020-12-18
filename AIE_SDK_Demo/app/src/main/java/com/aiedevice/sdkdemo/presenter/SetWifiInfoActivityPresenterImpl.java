package com.aiedevice.sdkdemo.presenter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.aiedevice.sdk.bluetooth.bean.BeanBlufiParams;
import com.aiedevice.sdkdemo.utils.NetworkUtil;
import com.aiedevice.sdkdemo.view.SetWifiInfoActivityView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

public class SetWifiInfoActivityPresenterImpl extends BasePresenter<SetWifiInfoActivityView>{
    public static final String TAG = SetWifiInfoActivityPresenterImpl.class.getSimpleName();
    private final WifiManager mWifiManager;
    private Context context;
    private List<ScanResult> scans;

    public SetWifiInfoActivityPresenterImpl(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        scans = new LinkedList<>();
    }


    public void getCurrentSSID(Context context) {
        if (!is5GHz(NetworkUtil.getConnectionFrequncy(context))) {
            if (getActivityView() != null) {
                getActivityView().showCurrentWifi(NetworkUtil.getConnectWifiSsid(context));
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                context.unregisterReceiver(this);
                Log.i(TAG, "scan wifi SCAN_RESULTS_AVAILABLE_ACTION " + intent);
                updateWifi();
                if (getActivityView() != null) {
                    getActivityView().showWifiList(scans);
                }
            }
        }
    };


    public void startScanWifi() {
        startScanWifi(true);
    }


    public void startScanWifi(boolean forceScan) {
        if (!mWifiManager.isWifiEnabled()) {
            if (getActivityView() != null) {
                getActivityView().showOpenWifiDialog();
            }
            return;
        }

        if (forceScan)
            scans.clear();
        else if (!scans.isEmpty()) {
            if (getActivityView() != null) {
                getActivityView().showWifiList(scans);
            }
            return;
        }

        context.registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // 由等待1.5秒改成接收广播
        mWifiManager.startScan();
        Log.i(TAG, "startScan Wifi");
    }


    public void stopScanWifi() {

    }


    public BeanBlufiParams getBlufiConfigureParam(BluetoothDevice device, String ssid, String pwd) {
        BeanBlufiParams params = new BeanBlufiParams(device, ssid, pwd);
        return params;
    }

    private synchronized void updateWifi() {
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        scans.clear();
        if (scanResults == null)
            return;

        Map<String, ScanResult> wifiMap = new HashMap<>(scanResults.size());
        for (ScanResult scanResult : scanResults) {
            Log.i(TAG, "[updateWifi] ssid=" + scanResult.SSID + " frequency=" + scanResult.frequency);
            if (TextUtils.isEmpty(scanResult.SSID))
                continue;

            if (wifiMap.get(scanResult.SSID) == null) {
                wifiMap.put(scanResult.SSID, scanResult);
            } else {
                if (!is5GHz(scanResult.frequency))
                    wifiMap.put(scanResult.SSID, scanResult);
            }
        }

        scans.addAll(wifiMap.values());
    }

    public static boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

    private String encode(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '#') {
                builder.append('\\');
            } else if (ch == '\\') {
                builder.append('\\');
            }
            builder.append(ch);
        }
        return builder.toString();
    }
}
