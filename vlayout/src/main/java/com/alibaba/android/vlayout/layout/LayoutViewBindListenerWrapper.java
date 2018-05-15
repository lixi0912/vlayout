package com.alibaba.android.vlayout.layout;

import android.view.View;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/14
 */
class LayoutViewBindListenerWrapper implements BaseLayoutHelper.LayoutViewBindListener, BaseLayoutHelper.LayoutViewUnBindListener {
    private BaseLayoutHelper.LayoutViewBindListener bindListener;
    private BaseLayoutHelper.LayoutViewUnBindListener unBindListener;

    LayoutViewBindListenerWrapper(BaseLayoutHelper.LayoutViewBindListener layoutViewBindListener, BaseLayoutHelper.LayoutViewUnBindListener layoutViewUnBindListener) {
        this.bindListener = layoutViewBindListener;
        this.unBindListener = layoutViewUnBindListener;
    }

    public void setBindListener(BaseLayoutHelper.LayoutViewBindListener bindListener) {
        this.bindListener = bindListener;
    }

    public BaseLayoutHelper.LayoutViewBindListener getBindListener() {
        return bindListener;
    }

    public BaseLayoutHelper.LayoutViewUnBindListener getUnBindListener() {
        return unBindListener;
    }

    public void setUnBindListener(BaseLayoutHelper.LayoutViewUnBindListener unBindListener) {
        this.unBindListener = unBindListener;
    }

    @Override
    public void onBind(View layoutView, BaseLayoutHelper baseLayoutHelper) {
        if (null != bindListener) {
            bindListener.onBind(layoutView, baseLayoutHelper);
        }
    }

    @Override
    public void onUnbind(View layoutView, BaseLayoutHelper baseLayoutHelper) {
        if (null != unBindListener) {
            unBindListener.onUnbind(layoutView, baseLayoutHelper);
        }
    }
}
