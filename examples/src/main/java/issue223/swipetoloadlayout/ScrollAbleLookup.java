package issue223.swipetoloadlayout;

import android.os.Build.VERSION;
import android.view.View;
import android.widget.AbsListView;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2017/9/12
 */

public interface ScrollAbleLookup {


    View onViewCaptured(SwipeToLoadLayout frame, View targetView);

    boolean canScrollVertically(SwipeToLoadLayout frame, View targetView, int direction);


    class Default implements ScrollAbleLookup {



        @Override
        public View onViewCaptured(SwipeToLoadLayout frame, View targetView) {
            return targetView;
        }

        @Override
        public boolean canScrollVertically(SwipeToLoadLayout frame, View targetView, int direction) {
            if (null == targetView)
                return false;
            if (VERSION.SDK_INT < 14) {
                if (targetView instanceof AbsListView) {
                    final AbsListView absListView = (AbsListView) targetView;
                    if (direction < 0) {
                        return absListView.getChildCount() > 0
                                && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                                .getTop() < absListView.getPaddingTop());
                    } else {
                        return absListView.getChildCount() > 0
                                && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                                || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
                    }
                } else {
                    return targetView.canScrollVertically(direction) || (direction > 0 ?
                            targetView.getScrollY() < 0 : targetView.getScrollY() > 0);
                }
            } else {
                return targetView.canScrollVertically(direction);
            }

        }
    }
}
