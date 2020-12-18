package com.aiedevice.sdkdemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

public class PermissionHelper {
    private final int REQ_PERMISSION_SMS = 1;
    private final int REQ_PERMISSION_PHONE = 2;
    private final int REQ_PERMISSION_MICROPHONE = 3;
    private final int REQ_PERMISSION_STORAGE = 4;
    private final int REQ_PERMISSION_LOCATION = 5;

    private Activity context;
    private PermissionRequestCallBack permissionCallback;

    public PermissionHelper(Activity context) {
        this.context = context;
    }

    public boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface PermissionRequestCallBack {
        void onPermissionGrant(String[] permissionNames);

        void onPermissionNoGrant(String[] permissionNames);
    }

    public void requestCameraAndMicrophonePermission(PermissionRequestCallBack callback) {
        this.permissionCallback = callback;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},
                    REQ_PERMISSION_MICROPHONE);
        } else {
            if (this.permissionCallback != null) {
                this.permissionCallback.onPermissionGrant(new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA});
            }
        }
    }

    public void requestPhonePermission(PermissionRequestCallBack callback) {
        this.permissionCallback = callback;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_PHONE_STATE}, REQ_PERMISSION_PHONE);
        } else {
            if (this.permissionCallback != null) {
                this.permissionCallback.onPermissionGrant(new String[]{Manifest.permission.READ_PHONE_STATE});
            }
        }
    }

    public void requestStoragePermission(PermissionRequestCallBack callback) {
        this.permissionCallback = callback;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQ_PERMISSION_STORAGE);
        } else {
            if (this.permissionCallback != null) {
                this.permissionCallback.onPermissionGrant(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        }
    }

    public void requestSmsPermission(PermissionRequestCallBack callback) {
        this.permissionCallback = callback;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
            }, REQ_PERMISSION_SMS);
        } else {
            if (this.permissionCallback != null) {
                this.permissionCallback.onPermissionGrant(new String[]{
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
                });
            }
        }
    }

//    public void requestLocationPermission(PermissionRequestCallBack callBack) {
//        this.permissionCallback = callBack;
//        if (ContextCompat.checkSelfPermission(context,
//                Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(context, new String[]{
//                    Manifest.permission_group.LOCATION}, REQ_PERMISSION_LOCATION);
//        } else {
//            if (this.permissionCallback != null) {
//                this.permissionCallback.onPermissionGrant(new String[]{Manifest.permission_group.LOCATION});
//            }
//        }
//    }

    public void requestLocationPermission(PermissionRequestCallBack callBack) {
        this.permissionCallback = callBack;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_PERMISSION_LOCATION);
        } else {
            if (this.permissionCallback != null) {
                this.permissionCallback.onPermissionGrant(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        }
    }

    public boolean isPermissionGranted(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION_SMS:
            case REQ_PERMISSION_STORAGE:
            case REQ_PERMISSION_PHONE:
            case REQ_PERMISSION_MICROPHONE:
                if (permissionCallback != null) {
                    if (verifyPermissions(grantResults)) {
                        permissionCallback.onPermissionGrant(permissions);
                    } else {
                        permissionCallback.onPermissionNoGrant(permissions);
                    }
                    permissionCallback = null;
                }
                break;
            default:
                break;
        }
    }
}
