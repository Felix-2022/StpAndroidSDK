package com.aiedevice.sdkdemo.push;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.aiedevice.sdkdemo.bean.GlobalVars;
import com.aiedevice.sdkdemo.utils.Toaster;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import java.nio.charset.Charset;


public class StpIntentService extends GTIntentService {
    private static final String TAG = StpIntentService.class.getSimpleName();
    private Handler mHandler;

    public StpIntentService() {
        super();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    // 处理透传消息
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String msgData = new String(msg.getPayload(), Charset.forName("UTF-8"));
        Log.d(TAG, "onReceiveMessageData -> " + "msgData = " + msgData);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toaster.show(msgData);
            }
        });
    }

    // 接收 cid
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.i(TAG, "onReceiveClientId -> " + "clientid = " + clientid + " isLogin=" + GlobalVars.isLogin());
        GlobalVars.setPushId(clientid);
    }

    // cid 离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.e(TAG, "onReceiveOnlineState -> " + "online = " + online);
    }

    // 各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.e(TAG, "onReceiveCommandResult -> " + "cmdMessage = " + cmdMessage);
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {
        Log.d(TAG, "[onNotificationMessageArrived] gtNotificationMessage=" + gtNotificationMessage);
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
        Log.d(TAG, "[onNotificationMessageClicked] gtNotificationMessage=" + gtNotificationMessage);
    }

}
