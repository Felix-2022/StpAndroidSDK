package com.aiedevice.sdkdemo.presenter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.aiedevice.sdk.base.bean.BeanResult;
import com.aiedevice.sdk.base.net.ResultListener;
import com.aiedevice.sdk.bluetooth.BluetoothBinderHelper;
import com.aiedevice.sdk.bluetooth.bean.BeanBlufiParams;
import com.aiedevice.sdkdemo.view.SendWifiInfoActivityView;

public class SendWifiInfoActivityPresenterImpl extends BasePresenter<SendWifiInfoActivityView> {
    private String TAG = SendWifiInfoActivityPresenterImpl.class.getSimpleName();

    private Context context;
    private BluetoothBinderHelper mBluetoothBindHelper;

    public SendWifiInfoActivityPresenterImpl(Context context) {
        mBluetoothBindHelper = new BluetoothBinderHelper(context);

        this.context = context;
    }


    public void startConfigure(BluetoothDevice device, BeanBlufiParams params) {
        mBluetoothBindHelper.startConfigure(context, device, params, new ResultListener() {
            @Override
            public void onSuccess(BeanResult beanResult) {
                Log.i(TAG, "[startConfigure-succ] result=" + beanResult.getResult());
                String mainCtl = beanResult.getData();
                if (getActivityView() != null)
                    getActivityView().onSendWifiSuccessful(mainCtl);
            }

            @Override
            public void onError(int errCode, String errMsg) {
                Log.i(TAG, "[startConfigure-fail] errCode=" + errCode + " errMsg=" + errMsg);
                if (getActivityView() != null)
                    getActivityView().onSendWifiFailure(errCode, errMsg);
            }
        });
    }

    public void stopConfigure() {
        mBluetoothBindHelper.stopConfigure();
    }
}
