package com.aiedevice.sdkdemo.activity.blufi;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiedevice.sdkdemo.BlufiConstants;
import com.aiedevice.sdkdemo.R;
import com.aiedevice.sdkdemo.activity.StpBaseActivity;
import com.aiedevice.sdkdemo.adapter.BTAdapter;
import com.aiedevice.sdkdemo.bean.EspBleDevice;
import com.aiedevice.sdkdemo.presenter.SearchDeviceActivityPresenterImpl;
import com.aiedevice.sdkdemo.view.SearchDeviceActivityView;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchDeviceActivity extends StpBaseActivity implements SearchDeviceActivityView {

    // logic
    private SearchDeviceActivityPresenterImpl presenter;
    private BTAdapter btAdapter;
    private Handler mMainHandler;


    // view
    @BindView(R.id.tv_state_search)
    TextView tvStateSearch;
    @BindView(R.id.btn_action)
    TextView btnAction;
    @BindView(R.id.rv_searched_devices)
    RecyclerView rvSearchedDevices;
    @BindView(R.id.ll_searching)
    LinearLayout llSearching;
    @BindView(R.id.ll_search_result)
    LinearLayout llSearchResult;
    @BindView(R.id.iv_status)
    ImageView ivStatus;

    public static void launch(Context context) {
        Intent intent = new Intent(context, SearchDeviceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPresenter();

        initView();
        initLogic();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search_device;
    }

    private void initView() {
        rvSearchedDevices.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,
                false));
        btAdapter = new BTAdapter(presenter.getEspBleDevices(), getApplicationContext());
        rvSearchedDevices.setAdapter(btAdapter);
    }

    private void initLogic() {
        mMainHandler = new Handler();
        showSearching();

        //支持配置要扫描的设备前缀名
        presenter.startScan(BlufiConstants.BLUFI_PREFIX);
    }

    private void initHeader() {
        setTitle("点读笔配网");
    }

    protected void attachPresenter() {
        presenter = new SearchDeviceActivityPresenterImpl(getApplicationContext());
        presenter.attachView(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void detachPresenter() {
        presenter.detachView();
        presenter.stopScan();
        presenter = null;
    }

    @Override
    public void showSearching() {
        tvStateSearch.setText("正在查找设备…");
        llSearching.setVisibility(View.VISIBLE);
        ivStatus.setImageResource(R.drawable.config_net_searching);
        llSearchResult.setVisibility(View.GONE);
    }

    @Override
    public void showNoDevice() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvStateSearch.setText("未搜索到设备");
                llSearching.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(R.drawable.config_net_notfound_device);
                llSearchResult.setVisibility(View.VISIBLE);
                rvSearchedDevices.setVisibility(View.GONE);
                btnAction.setText("重新搜索");
            }
        });
    }

    @Override
    public void onFindDevice(EspBleDevice device) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvStateSearch.setText("查找到以下设备");
                llSearching.setVisibility(View.VISIBLE);
                llSearchResult.setVisibility(View.VISIBLE);
                rvSearchedDevices.setVisibility(View.VISIBLE);
                btnAction.setText("继续");
                btAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void gotoSetWifiInfoActivity(BluetoothDevice device) {
        SetWifiInfoActivity.launch(this, device);
    }

    @OnClick({R.id.btn_action, R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_action:
                presenter.doNextStep();
                break;

            case R.id.iv_back:
                finish();
                break;
        }
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
