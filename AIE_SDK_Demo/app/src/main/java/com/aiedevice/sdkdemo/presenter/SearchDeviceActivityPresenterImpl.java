package com.aiedevice.sdkdemo.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.aiedevice.sdkdemo.BlufiConstants;
import com.aiedevice.sdkdemo.bean.EspBleDevice;
import com.aiedevice.sdkdemo.view.SearchDeviceActivityView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SearchDeviceActivityPresenterImpl extends BasePresenter<SearchDeviceActivityView>  {
    public static final long TIMEOUT_SCAN = 15;
    public static final String TAG = "SearchDeviceActivity";
    public static final int WHAT_STOP_SCAN = 1;
    private static int STATE_SEARCHING = 0;
    private static int STATE_NO_DEVICE = 1;
    private static int STATE_FOUND_DEVICE = 2;
    private Context mContext;
    private List<EspBleDevice> mBTList;
    private int currentState = STATE_NO_DEVICE;
    private ScanHandler mHandler;
    private String mSearchPrefix;

    private BluetoothAdapter.LeScanCallback mBTCallback = new BluetoothAdapter.LeScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public synchronized void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String deviceName = device.getName();

            if (TextUtils.isEmpty(mSearchPrefix)) {
                if (deviceName == null
                        || !(deviceName.startsWith(BlufiConstants.BLUFI_PREFIX) || deviceName.startsWith(BlufiConstants.OLD_BLUFI_PREFIX))
                        || mBTList.size() >= 1) {
                    return;
                }
            } else {
                if (deviceName == null || !deviceName.startsWith(mSearchPrefix) || mBTList.size() >= 1) {
                    return;
                }
            }

            Log.i(TAG, "find BLUFI Device:" + deviceName +
                    " address:>>>" + device.getAddress() +
                    " type:>>>" + device.getType());

            EspBleDevice newDevice = new EspBleDevice(device);
            newDevice.rssi = rssi;
            if (!containBle(newDevice.device)) {
                //现在只考虑一个设备
                mHandler.removeMessages(WHAT_STOP_SCAN);
                BluetoothAdapter.getDefaultAdapter().stopLeScan(mBTCallback);
                if (mBTList.size() == 0)//第一个设备默认选中
                    newDevice.checked = true;
                Log.i(TAG, "add Device:");
                mBTList.add(newDevice);
                getActivityView().onFindDevice(newDevice);

                currentState = STATE_FOUND_DEVICE;
            }
        }
    };

    public SearchDeviceActivityPresenterImpl(Context context) {
        this.mContext = context;
        mBTList = new ArrayList<>();
        mHandler = new ScanHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public void startScan() {
        currentState = STATE_SEARCHING;
        BluetoothAdapter.getDefaultAdapter().startLeScan(mBTCallback);
        mHandler.sendEmptyMessageDelayed(WHAT_STOP_SCAN, TIMEOUT_SCAN * 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public void startScan(String devicePrefix) {
        mSearchPrefix = devicePrefix;
        startScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public void stopScan() {
        mHandler.removeMessages(WHAT_STOP_SCAN);
        BluetoothAdapter.getDefaultAdapter().stopLeScan(mBTCallback);
    }


    public List<EspBleDevice> getEspBleDevices() {
        return mBTList;
    }


    public void doNextStep() {
        if (getActivityView() == null) {
            return;
        }
        if (currentState == STATE_NO_DEVICE) {
            getActivityView().showSearching();
            startScan();
        } else if (currentState == STATE_FOUND_DEVICE) {
            for (EspBleDevice device : mBTList) {
                if (device.checked) {
                    getActivityView().gotoSetWifiInfoActivity(device.device);
                    return;
                }
            }

            getActivityView().showError("没有选择设备");
        }
    }

    private boolean containBle(BluetoothDevice device) {
        for (EspBleDevice ble : mBTList) {
            if (ble.device.equals(device)) {
                return true;
            }
        }

        return false;
    }

    private void onScanTimeout() {
        Log.i(TAG, "scan onCompleted:" + mBTList.size());
        if (mBTList.isEmpty()) {
            if (getActivityView() == null)
                return;

            if (getActivityView() != null) {
                getActivityView().showNoDevice();
            }
            currentState = STATE_NO_DEVICE;
        }
    }

    private static class ScanHandler extends Handler {
        private WeakReference<SearchDeviceActivityPresenterImpl> presenterWeakReference;

        public ScanHandler(SearchDeviceActivityPresenterImpl presenter) {
            super(Looper.getMainLooper());
            this.presenterWeakReference = new WeakReference<SearchDeviceActivityPresenterImpl>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SearchDeviceActivityPresenterImpl presenter = presenterWeakReference.get();
            if (null == presenter)
                return;
            presenter.stopScan();
            presenter.onScanTimeout();
        }
    }
}
