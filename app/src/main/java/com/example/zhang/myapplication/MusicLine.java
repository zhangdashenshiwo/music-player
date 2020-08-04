package com.example.zhang.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class MusicLine extends View {


    private Paint mPaint;
    private float mWidth;
    private float mRectWidth;
    private float mRectHeight;
    private float mRectCount = 25;
    private int offsets = 2;
    private LinearGradient mLinearGradient;
    private double mRandom;

    public MusicLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }

    public MusicLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MusicLine(Context context) {
        super(context);
        initView();
    }

    /**
     * 初始化工具类
     */
    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    /**
     * 确定示波器的高度，宽度，传递给画布，将其显示出来
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mRectHeight = getHeight();
        mRectWidth = (int) (mWidth * 0.6 / mRectCount);
        mLinearGradient = new LinearGradient(0, 0, mRectWidth, mRectHeight,
                0xFFE4E44E, 0xFF6848EB, Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);

    }


    /**
     * 调取画布，实现示波器的显示
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mRectCount; i++) {
            mRandom = Math.random();
            float currentHeight = (float) (mRectHeight * mRandom);
            canvas.drawRect(
                    (float) (mWidth * 0.4 / 2 + mRectWidth * i + offsets),
                    currentHeight, (float) (mWidth * 0.4 / 2 + mRectWidth
                            * (i + 1)), mRectHeight, mPaint);
        }

    }

    /**
     * 定时更新画布
     */
    public void onStart() {
        postInvalidateDelayed(100);
    }

}

