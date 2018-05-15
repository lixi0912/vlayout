package com.alibaba.android.vlayout.layout;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.LayoutManagerHelper;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/14
 */
class ItemAnimatorCompat {
    private static final String TAG = "ItemAnimatorCompat";


    public static boolean supportAnimator(RecyclerView recyclerView, final LayoutManagerHelper helper, BaseLayoutHelper baseLayoutHelper, int positionStart, int itemCount, View mLayoutView) {
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();


        return null != itemAnimator;
        //        if (null == itemAnimator) {
//            return false;
//        }
//        if (itemAnimator instanceof ItemAnimatorWatcher) {
//            if (null != mLayoutView) {
//                Rect removedViewRect = baseLayoutHelper.getRemovedViewRect();
//
//                // skip layout view
//                positionStart++;
//
//                int lastBottom = 0;
//                long durationMillis = itemAnimator.getRemoveDuration();
//
//                if (helper.getOrientation() == VirtualLayoutManager.VERTICAL) {
//                    int childCount = recyclerView.getChildCount();
//                    for (int i = 0, pos = positionStart; i < itemCount && i < childCount; i++, pos++) {
//                        View child = recyclerView.getChildAt(pos);
//                        if (null != child) {
//                            removedViewRect.union(child.getLeft(), child.getTop(),
//                                    child.getRight(), child.getBottom());
//
//                            if (lastBottom == 0) {
//                                lastBottom = child.getBottom();
//                            } else if (lastBottom != child.getBottom()) {
//                                // new line
//                                durationMillis += itemAnimator.getRemoveDuration();
//                            }
//                        }
//                    }
//                    if (BuildConfig.DEBUG) {
//                        Log.d(TAG, "rect:" + removedViewRect);
//                    }
//
//                    final ViewOffsetHelper offsetHelper = new ViewOffsetHelper(mLayoutView);
//                    ObjectAnimator offsetYAni = ObjectAnimator.ofInt(offsetHelper, "offsetY",
//                            offsetHelper.getOffsetY(), removedViewRect.height());
//                    offsetYAni.setDuration(durationMillis);
//                    offsetHelper.setAnimator(offsetYAni);
//
//                    removedViewRect.set(0, 0, 0, 0);
//
//
//                    final ItemAnimatorWatcher watcher = (ItemAnimatorWatcher) itemAnimator;
//                    watcher.setItemAnimatorStartedListener(new ItemAnimatorWatcher.StartedListener() {
//                        @Override
//                        public void onAnimationStarted(RecyclerView.ViewHolder viewHolder) {
//                            offsetHelper.start();
//                            watcher.setItemAnimatorStartedListener(null);
//                        }
//                    });
//
//                    return true;
//                } else {
//                    // TODO fix bg-color
//                    return false;
//                }
//            }
//        } else {
//
//
//        }
//        return false;
    }
}
