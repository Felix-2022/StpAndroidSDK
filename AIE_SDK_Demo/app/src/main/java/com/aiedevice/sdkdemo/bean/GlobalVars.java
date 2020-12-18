package com.aiedevice.sdkdemo.bean;

public class GlobalVars {
    private static boolean isLogin = false;
    private static String pushId = "";

    public static void setLoginStatus(boolean flag) {
        isLogin = flag;
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public static void setPushId(String pushId) {
        GlobalVars.pushId = pushId;
    }

    public static String getPushId() {
        return pushId;
    }

}
