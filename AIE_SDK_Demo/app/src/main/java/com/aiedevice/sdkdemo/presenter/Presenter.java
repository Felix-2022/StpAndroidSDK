package com.aiedevice.sdkdemo.presenter;

import com.aiedevice.sdkdemo.view.BaseView;

public interface Presenter<V extends BaseView> {

    void attachView(V baseView);

    void detachView();
}
