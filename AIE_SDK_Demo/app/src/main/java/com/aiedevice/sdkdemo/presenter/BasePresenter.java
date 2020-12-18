package com.aiedevice.sdkdemo.presenter;

import com.aiedevice.sdkdemo.view.BaseView;

public class BasePresenter<T extends BaseView> implements Presenter<T> {

    private T mActivityView;

    @Override
    public void attachView(T activityView) {
        mActivityView = activityView;
    }

    @Override
    public void detachView() {
        mActivityView = null;
    }

    public boolean isViewAttached() {
        return mActivityView != null;
    }

    public T getActivityView() {
        return mActivityView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new BaseViewNotAttachedException();
    }

    public static class BaseViewNotAttachedException extends RuntimeException {
        public BaseViewNotAttachedException() {
            super("Please call Presenter.attachView(BaseView) before" + " requesting data to the Presenter");
        }
    }
}

