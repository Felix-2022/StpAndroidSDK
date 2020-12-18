package com.aiedevice.sdkdemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import static android.content.Context.WIFI_SERVICE;

public class NetworkUtil {

    private static final String TAG = "NetworkUtil";
    public static final int NETWORK_STATE_MOBILE = 0;
    public static final int NETWORK_STATE_WIFI = 1;
    public static final int NETWORK_STATE_NO_NET = 2;

    public static final String MOBILE = "mobile";
    public static final String WIFI = "wifi";

    public static String getMac(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return null;
        }
        WifiInfo info = manager.getConnectionInfo();
        if (info == null) {
            return null;
        }
        return info.getMacAddress();
    }

    public static String getNetState(Context context) {
        int netState = getNetworkState(context);
        if (netState == NETWORK_STATE_MOBILE) {
            return MOBILE;
        } else if (netState == NETWORK_STATE_WIFI) {
            return WIFI;
        }
        return "";
    }

    public static int getNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Log.i(TAG, "current not have net");
            return NETWORK_STATE_NO_NET;
        }

        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null) {
            State state = wifiInfo.getState();
            if (State.CONNECTED == state) {
                Log.i(TAG, "current net state is wifi");
                return NETWORK_STATE_WIFI;
            }
        }

        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null) {
            State state = mobileInfo.getState();
            if (State.CONNECTED == state) {
                Log.i(TAG, "current net state is mobile");
                return NETWORK_STATE_MOBILE;
            }
        }

        Log.i(TAG, "not get useful network message,return not have net");
        return NETWORK_STATE_NO_NET;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }

    public static String getConnectWifiSsid(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
            return "";

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null)
            return "";
        return wifiInfo.getSSID();
    }


    public static int getConnectionFrequncy(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return -1;
        }

        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        if (!mWifiManager.isWifiEnabled()) {
            return -1;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return -1;
        }

        return wifiInfo.getFrequency();
    }

}