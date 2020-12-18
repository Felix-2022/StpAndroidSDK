package com.aiedevice.sdkdemo.view;

public interface BaseView {

    void showLoading(String msg);

    void hideLoading();

    void showError(String msg);

    void showEmpty(String msg);

}
