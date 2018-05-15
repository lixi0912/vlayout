package com.alibaba.android.vlayout.layout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

import com.alibaba.android.vlayout.BuildConfig;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/14
 */
class ViewOffsetHelper {

    private static final String TAG = "ViewOffsetHelper";

    private View view;


    private int mOffsetX;
    private int mOffsetY;
    private Animator animator;

    ViewOffsetHelper(View view) {
        setView(view);

    }

    public void setView(View view) {
        this.view = view;
        if (null != view) {
            this.mOffsetX = 0;
            this.mOffsetY = 0;
        }
    }

    public int getOffsetX() {
        return mOffsetX;
    }

    public void setOffsetX(int mOffsetX) {
        if (null != view) {
            int dx = mOffsetX - this.mOffsetX;
            view.layout(0, 0,
                    view.getRight() - dx,
                    view.getBottom());

        }
        this.mOffsetX = mOffsetX;
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    public void setOffsetY(int offsetY) {
        if (null != view) {
            int dy = offsetY - this.mOffsetY;
            view.layout(0, 0, view.getRight(), view.getBottom() - dy);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "offsetY:" + offsetY +
                        ",mOffsetY:" + mOffsetY +
                        ",dy:" + dy + ",view.Bottom:" + view.getBottom()
                );
            }
        }
        this.mOffsetY = offsetY;
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }

    public void start() {
        if (null != animator && !animator.isStarted()) {
            animator.start();
        }
    }
}
