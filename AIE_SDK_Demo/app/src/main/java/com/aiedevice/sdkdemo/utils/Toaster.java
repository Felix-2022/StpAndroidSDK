package com.aiedevice.sdkdemo.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.aiedevice.sdk.StpSDK;


public class Toaster {
    private static Toast mToast;

    public static void show(int resId) {
        show(StpSDK.getInstance().getContext().getString(resId));
    }

    public static void show(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            showToast(text);
        }
    }

    private static void showToast(CharSequence text) {
        Context context = StpSDK.getInstance().getContext();

        if (Build.VERSION.SDK_INT >= 9) {
            Toast toast = Toast.makeText((Context) context, text, Toast.LENGTH_SHORT);
            toast.setGravity(17, 0, 0);
            toast.show();
            return;
        }
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText((Context) context, text, Toast.LENGTH_SHORT);
            mToast.setGravity(17, 0, 0);
        }
        mToast.show();
    }
}