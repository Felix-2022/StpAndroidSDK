package com.aiedevice.sdkdemo.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import com.aiedevice.sdkdemo.view.PreConnectActivityView;
import com.aiedevice.sdkdemo.R;

import static android.content.Context.LOCATION_SERVICE;

public class PreConnectActivityPresenterImpl extends BasePresenter<PreConnectActivityView>  {
    public static final String TAG = PreConnectActivityPresenterImpl.class.getSimpleName();
    private Context context;

    public PreConnectActivityPresenterImpl(Context context) {
        this.context = context;
    }


    public void validConnectConditions() {
        if (getActivityView() == null) {
            return;
        }
        //Android 4.3之后才支持BLE
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getActivityView().showError(context.getString(R.string.not_support_ble));
        } else {
            //有的手机可能硬件就不支持BLE
            boolean isSupportBLE = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
            if (!isSupportBLE) {
                getActivityView().showError(context.getString(R.string.not_support_ble));
                return;
            }

            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                getActivityView().showOpenBluetoothDialog();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Check location enable  6.0以后BLE搜索还需要打开位置服务
                    LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                    boolean locationGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean locationNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if (!locationGPS && !locationNetwork) {
                        getActivityView().showOpenLocationDialog();
                    } else {
                        getActivityView().gotoFindDeviceActivity();
                    }
                } else {
                    getActivityView().gotoFindDeviceActivity();
                }
            }
        }
    }

    public void startBluetoothStateChangeMonitor() {
        context.registerReceiver(mReceiver, makeFilter());
    }

    public void stopBluetoothStateChangeMonitor() {
        try {
            context.unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }
    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "[onReceive] action=" + intent.getAction());
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.i(TAG, "onReceive---------STATE_TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.i(TAG, "onReceive---------STATE_ON");
//                            validConnectConditions();
                            getActivityView().gotoFindDeviceActivity();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.i(TAG, "onReceive---------STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.i(TAG, "onReceive---------STATE_OFF");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
