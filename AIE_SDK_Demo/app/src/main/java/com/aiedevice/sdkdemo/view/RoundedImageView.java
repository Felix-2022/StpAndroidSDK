package com.aiedevice.sdkdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.aiedevice.sdkdemo.R;

import java.util.logging.Logger;

/**
 * bind
 * Created by macro on 16/3/18.
 */
public class RoundedImageView extends View {

    private Logger mLogger = Logger.getLogger(RoundedImageView.class.getSimpleName());
    private Paint mPaint;
    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private int mResourceId;

    public RoundedImageView(Context context) {
        super(context);

        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
        mResourceId = array.getResourceId(R.styleable.RoundedImageView_src, 0);
        mLogger.info("mResourceId: " + mResourceId);
        array.recycle();

        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        setWillNotDraw(false);

        if (mResourceId != 0) {
            mBitmap = BitmapFactory.decodeResource(getResources(), mResourceId);
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
    }

    private float dp2px(int dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }

    private Bitmap createRoundConerImage(Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        RectF rect = new RectF(0, 0, mWidth, mHeight);
        canvas.drawRoundRect(rect, dp2px(8), dp2px(8), paint);
        canvas.drawRoundRect(new RectF(4, 4, mWidth - 4, mHeight - 4), dp2px(8), dp2px(8), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, null, rect, paint);
        return target;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null) {
            canvas.drawBitmap(createRoundConerImage(mBitmap), 0, 0, mPaint);
        }
    }
}
