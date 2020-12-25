package com.aiedevice.sdkdemo.activity.blufi;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiedevice.sdk.bluetooth.bean.BeanBlufiParams;
import com.aiedevice.sdkdemo.R;
import com.aiedevice.sdkdemo.activity.MainActivity;
import com.aiedevice.sdkdemo.activity.StpBaseActivity;
import com.aiedevice.sdkdemo.presenter.SendWifiInfoActivityPresenterImpl;
import com.aiedevice.sdkdemo.view.SendWifiInfoActivityView;

import butterknife.BindView;
import butterknife.OnClick;

public class SendWifiInfoActivity extends StpBaseActivity implements SendWifiInfoActivityView {
    private static final String TAG = SendWifiInfoActivity.class.getSimpleName();
    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    public static final String EXTRA_PARAM = "EXTRA_PARAM";

    // logic
    private BluetoothDevice mDevice;
    private BeanBlufiParams mParams;
    private SendWifiInfoActivityPresenterImpl presenter;
    private Handler uiHandler;

    // view
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.btn_retry)
    Button btnRetry;

    public static void launch(Context context, BluetoothDevice device,
                              BeanBlufiParams configureParams) {
        Intent intent = new Intent(context, SendWifiInfoActivity.class);
        intent.putExtra(EXTRA_DEVICE, device);
        intent.putExtra(EXTRA_PARAM, configureParams);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPresenter();

        mDevice = getIntent().getParcelableExtra(EXTRA_DEVICE);
        mParams = (BeanBlufiParams) getIntent().getSerializableExtra(EXTRA_PARAM);
        initLogic();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_send_wifi_info;
    }

    private void initLogic() {
        uiHandler = new Handler();

        presenter.startConfigure(mDevice, mParams);
    }

    protected void attachPresenter() {
        presenter = new SendWifiInfoActivityPresenterImpl(getApplicationContext());
        presenter.attachView(this);
    }

    protected void detachPresenter() {
        presenter.detachView();
        presenter.stopConfigure();
        presenter = null;
    }

    @Override
    public void onSendWifiSuccessful(String mainCtl) {
        tvState.setText("恭喜！ 设备连成功");

        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.launch(SendWifiInfoActivity.this);
                finish();
            }
        }, 2000);
    }

    @OnClick({R.id.btn_retry, R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onSendWifiFailure(int errCode, String errMsg) {
        Log.e(TAG, "[onSendWifiFailure] errCode=" + errCode + " errMsg=" + errMsg);
        tvState.setText("联网失败. 错误：" + errMsg);
        ivStatus.setImageResource(R.drawable.config_net_connect_fail);
        btnRetry.setVisibility(View.VISIBLE);
        presenter.stopConfigure();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachPresenter();
    }
}
