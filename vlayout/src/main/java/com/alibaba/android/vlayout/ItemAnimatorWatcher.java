package com.alibaba.android.vlayout;

import android.support.v7.widget.RecyclerView;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/14
 */
public interface ItemAnimatorWatcher {
    void setItemAnimatorStartedListener(StartedListener listener);


    StartedListener getItemAnimatorStartedListener();

    /**
     * @author 陈晓辉
     * @description <>
     * @date 2018/5/14
     */
    interface StartedListener {


        /**
         * @param viewHolder item view holder
         */
        void onAnimationStarted(RecyclerView.ViewHolder viewHolder);

    }
}
