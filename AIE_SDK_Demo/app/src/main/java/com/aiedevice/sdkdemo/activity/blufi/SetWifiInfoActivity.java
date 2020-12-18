package com.aiedevice.sdkdemo.activity.blufi;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import com.aiedevice.sdk.bluetooth.bean.BeanBlufiParams;
import com.aiedevice.sdkdemo.R;
import com.aiedevice.sdkdemo.activity.StpBaseActivity;

import com.aiedevice.sdkdemo.presenter.SetWifiInfoActivityPresenterImpl;
import com.aiedevice.sdkdemo.utils.PermissionHelper;
import com.aiedevice.sdkdemo.utils.Toaster;
import com.aiedevice.sdkdemo.view.MyEditText;
import com.aiedevice.sdkdemo.view.SetWifiInfoActivityView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SetWifiInfoActivity extends StpBaseActivity implements SetWifiInfoActivityView {
    private static final String TAG = SetWifiInfoActivity.class.getSimpleName();
    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    public static final int REQUEST_CODE = 1;

    // logic
    private SetWifiInfoActivityPresenterImpl presenter;
    private BluetoothDevice mDevice;
    private PermissionHelper mPermissionHelper;
    private ScanResult mSelectedResult = null;

    // view
    @BindView(R.id.et_ssid)
    MyEditText etSSID;
    @BindView(R.id.et_passwd)
    MyEditText etPasswd;

    public static void launch(Context context, BluetoothDevice device) {
        Intent intent = new Intent(context, SetWifiInfoActivity.class);
        intent.putExtra(EXTRA_DEVICE, device);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPresenter();

        mDevice = getIntent().getParcelableExtra(EXTRA_DEVICE);

        initView();
        initLogic();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_set_wifi_info;
    }

    private void initView() {
        etSSID.setImgListener(new MyEditText.EdittextImgListener() {
            @Override
            public void onRightImgClick() {
                presenter.startScanWifi();
            }
        });

        etPasswd.setImgListener(new MyEditText.EdittextImgListener() {
            @Override
            public void onRightImgClick() {
                Boolean flag = (Boolean) etPasswd.getTag();
                if (flag == null || flag == true) {
                    flag = false;
                    etPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    etPasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = true;
                }

                etPasswd.setTag(flag);
            }
        });
    }

    private void initLogic() {
        presenter.getCurrentSSID(getApplicationContext());
        mPermissionHelper = new PermissionHelper(this);
    }

    protected void attachPresenter() {
        presenter = new SetWifiInfoActivityPresenterImpl(getApplicationContext());
        presenter.attachView(this);
    }

    protected void detachPresenter() {
        presenter.detachView();
        presenter = null;
    }

    @OnClick({R.id.btn_connect, R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    Toaster.show("请先打开蓝牙");
                    return;
                }

                String ssid = etSSID.getText().toString();
                String pwd = etPasswd.getText().toString();
                if (TextUtils.isEmpty(ssid)) {
                    Toaster.show("请选择WiFi");
                    etSSID.requestFocus();
                    return;
                }

                if (mSelectedResult != null && TextUtils.equals(mSelectedResult.SSID, ssid)
                        && SetWifiInfoActivityPresenterImpl.is5GHz(mSelectedResult.frequency)) {
                    Toaster.show("抱歉，暂不支持5G WiFi，请切换其他网络");
                    return;
                }

                if (pwd == null || pwd.length() == 0) {
                    Log.w(TAG, "[onViewClick] password is empty");
                } else if (pwd.length() < 8) {
                    Toaster.show("请输入正确的密码,密码长度大于8位");
                } else {
                    launchNextPage(ssid, pwd);
                    finish();
                }
                break;

            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void launchNextPage(String ssid, String pwd) {
        BeanBlufiParams configureParam = presenter.getBlufiConfigureParam(mDevice, ssid, pwd);
        SendWifiInfoActivity.launch(this, mDevice, configureParam);
    }

    @Override
    public void showWifiList(final List<ScanResult> mWifiList) {
        int count = mWifiList.size();
        if (count == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !mPermissionHelper.isPermissionGranted(Manifest.permission_group.LOCATION)) {
                Toaster.show("App没有定位权限");
            } else
                Toaster.show("附近没有查找到ＷiFi");
            return;
        }

        int checkedItem = -1;
        String inputSsid = etSSID.getText().toString();
        final String[] wifiSSIDs = new String[count];
        for (int i = 0; i < count; i++) {
            ScanResult sr = mWifiList.get(i);
            wifiSSIDs[i] = sr.SSID;
            if (inputSsid.equals(sr.SSID)) {
                checkedItem = i;
            }
        }
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(wifiSSIDs, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedResult = mWifiList.get(which);
                        etSSID.setText(wifiSSIDs[which]);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void showCurrentWifi(String ssid) {
        etSSID.setText(ssid == null ? "" : ssid.replace("\"", ""));
    }

    @Override
    public void showOpenWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("WIFI开关没有打开，是否要打开WIFI");
        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        builder.setNeutralButton("不打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog openLocationDialog = builder.create();
        openLocationDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachPresenter();
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
