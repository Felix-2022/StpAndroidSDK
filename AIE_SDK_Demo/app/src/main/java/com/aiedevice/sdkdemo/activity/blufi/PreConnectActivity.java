package com.aiedevice.sdkdemo.activity.blufi;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.aiedevice.sdkdemo.R;
import com.aiedevice.sdkdemo.activity.StpBaseActivity;

import com.aiedevice.sdkdemo.presenter.PreConnectActivityPresenterImpl;
import com.aiedevice.sdkdemo.utils.PermissionHelper;
import com.aiedevice.sdkdemo.view.PreConnectActivityView;

import butterknife.OnClick;

public class PreConnectActivity extends StpBaseActivity implements PreConnectActivityView {
    private static final String TAG = PreConnectActivity.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_OPEN_LOCATION = 2;
    private static final int REQUEST_PERMISSION = 3;

    // logic
    private PreConnectActivityPresenterImpl mPresenter;

    private PermissionHelper mPermissionHelper;

    public static void launch(Context context) {
        Intent intent = new Intent(context, PreConnectActivity.class);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPresenter();

        initLogic();
    }

    private void initLogic() {
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.requestLocationPermission(new PermissionHelper.PermissionRequestCallBack() {
            @Override
            public void onPermissionGrant(String[] permissionNames) {
                Log.i(TAG, "[onPermissionGrant] permissionNames=" + permissionNames);
            }

            @Override
            public void onPermissionNoGrant(String[] permissionNames) {
                Log.i(TAG, "[onPermissionNoGrant] permissionNames=" + permissionNames);
            }
        });

    }

    @OnClick({R.id.btn_next, R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                mPresenter.validConnectConditions();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    protected void attachPresenter() {
        mPresenter = new PreConnectActivityPresenterImpl(getApplicationContext());
        mPresenter.attachView(this);
    }

    protected void detachPresenter() {
        mPresenter.detachView();
        mPresenter = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void showOpenBluetoothDialog() {
        mPresenter.startBluetoothStateChangeMonitor();

        if (!isBluetoothEnabled()) {
            turnOnBluetooth();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Check location enable  6.0以后BLE搜索还需要打开位置服务
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean locationGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean locationNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!locationGPS && !locationNetwork) {
                    if (isBluetoothEnabled()) {
                        showOpenLocationDialog();
                    }
                }
            }
        }
    }

    /**
     * 当前 Android 设备是否支持 Bluetooth
     *
     * @return true：支持 Bluetooth false：不支持 Bluetooth
     */
    public boolean isBluetoothSupported() {
        return BluetoothAdapter.getDefaultAdapter() != null ? true : false;
    }

    /**
     * 当前 Android 设备的 bluetooth 是否已经开启
     *
     * @return true：Bluetooth 已经开启 false：Bluetooth 未开启
     */
    public boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 强制开启当前 Android 设备的 Bluetooth
     *
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    public boolean turnOnBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        }
        return false;
    }

    @Override
    public void showOpenLocationDialog() {
        Log.i(TAG, "[showOpenLocationDialog]");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("配置网络需要打开位置服务");
        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_OPEN_LOCATION);
            }
        });

        builder.setNeutralButton("不打开试试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.stopBluetoothStateChangeMonitor();
                SearchDeviceActivity.launch(PreConnectActivity.this);
            }
        });

        AlertDialog openLocationDialog = builder.create();
        openLocationDialog.show();
    }

    @Override
    public void gotoFindDeviceActivity() {
        mPresenter.stopBluetoothStateChangeMonitor();
        SearchDeviceActivity.launch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachPresenter();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.act_pre_connect;
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void showEmpty(String msg) {

    }
}
