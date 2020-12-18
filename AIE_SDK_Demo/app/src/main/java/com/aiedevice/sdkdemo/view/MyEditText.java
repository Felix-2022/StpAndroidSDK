package com.aiedevice.sdkdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyEditText extends android.support.v7.widget.AppCompatEditText implements View.OnTouchListener {
    private static final String TAG = MyEditText.class.getSimpleName();

    private EdittextImgListener imgListener;

    //需要实现下面的几个构造函数，不然有可能加载不了这个EditText控件
    public MyEditText(Context context) {
        super(context);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //初始化控件，绑定监听器
    public void init() {
        setOnTouchListener(this);
    }

    public void setImgListener(EdittextImgListener listener) {
        imgListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //如果不是按下操作，就不做处理，如果是按下操作但是没有图片，也不做处理
        if (event.getAction() == MotionEvent.ACTION_UP && this.getCompoundDrawables()[2] != null) {
            //检测点击区域的X坐标是否在图片范围内
            if (event.getX() > this.getWidth()
                    - this.getPaddingRight()
                    - this.getCompoundDrawables()[2].getIntrinsicWidth()) {

                //在此做图片的点击处理
                this.clearFocus();
                this.setCursorVisible(false);
                if (imgListener != null) {
                    imgListener.onRightImgClick();
                }

                return true;
            }
            return false;
        }

        return false;
    }

    public interface EdittextImgListener {
        void onRightImgClick();
    }
}
