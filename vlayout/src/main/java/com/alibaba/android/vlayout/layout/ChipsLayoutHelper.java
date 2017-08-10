package com.alibaba.android.vlayout.layout;

import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2017/8/7
 */

public class ChipsLayoutHelper extends BaseLayoutHelper {

    private int mHGap;
    private int mVGap;

    public void setHGap(int mHGap) {
        this.mHGap = mHGap;
    }

    public void setVGap(int mVGap) {
        this.mVGap = mVGap;
    }

    private Span first = new Span();


    @Override
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state,
                            VirtualLayoutManager.LayoutStateWrapper layoutState,
                            LayoutChunkResult result, LayoutManagerHelper helper) {
        // reach the end of this layout
        if (isOutOfRange(layoutState.getCurrentPosition())) {
            return;
        }

        final boolean layoutInVertical = helper.getOrientation() == VERTICAL;
//        final int itemDirection = layoutState.getItemDirection();
//        final boolean layingOutInPrimaryDirection =
//                itemDirection == VirtualLayoutManager.LayoutStateWrapper.ITEM_DIRECTION_TAIL;

        int maxSize;
        if (layoutInVertical) {
            maxSize = helper.getContentWidth() - helper.getPaddingRight() - helper.getPaddingLeft()
                    - getHorizontalMargin() - getHorizontalPadding();
        } else {
            maxSize = helper.getContentHeight() - helper.getPaddingBottom() - helper.getPaddingTop()
                    - getVerticalMargin() - getVerticalPadding();
        }

        int hGap = this.mHGap;
        int vGap = this.mVGap;

        OrientationHelper orientationHelper = helper.getMainOrientationHelper();

        if (layoutInVertical) {
            Span span = first;
            span.clear();
            while (layoutState.hasMore(state) && !isOutOfRange(layoutState.getCurrentPosition())) {
                // find corresponding layout container
                View view = nextView(recycler, layoutState, helper, result);
                if (view == null) {
                    break;
                }
                if (view.getVisibility() == View.GONE) {
                    continue;
                }

                VirtualLayoutManager.LayoutParams params = (VirtualLayoutManager.LayoutParams) view.getLayoutParams();
                int heightSpec = helper.getChildMeasureSpec(
                        helper.getContentHeight() - helper.getPaddingTop() - helper.getPaddingBottom()
                                - getVerticalMargin() - getVerticalPadding(), params.height, true);

                int widthSpec = helper.getChildMeasureSpec(
                        helper.getContentWidth() - helper.getPaddingLeft() - helper.getPaddingRight()
                                - getHorizontalMargin() - getHorizontalPadding(), params.width, false);

                helper.measureChildWithMargins(view, widthSpec, heightSpec);


                int viewWidth = orientationHelper.getDecoratedMeasurementInOther(view);

                if ((maxSize - span.usedWidth - hGap < viewWidth) && !span.views.isEmpty()) {
                    // add to new line
                    span.totalSpacing = Math.max(maxSize - span.usedWidth, 0);
                    span = (span.next = new Span());
                }
                span.maxViewHeight = Math.max(span.maxViewHeight,
                        orientationHelper.getDecoratedMeasurement(view));
                span.usedWidth += hGap + viewWidth;
                span.views.add(view);
            }
            // add last line
            span.totalSpacing = maxSize - span.usedWidth;
            final int defaultNewViewLine = layoutState.getOffset();
            layoutVerticalChild(helper, orientationHelper, result, defaultNewViewLine, hGap, vGap);
        } else {
            // TODO
        }
    }

    private void layoutVerticalChild(LayoutManagerHelper helper, OrientationHelper orientationHelper,
                                     LayoutChunkResult result, int defaultNewViewLine, int hGap, int vGap) {
        int top = helper.getPaddingTop() + mMarginTop + mPaddingTop + defaultNewViewLine;
        int bottom = 0;

        final int defaultLeft = helper.getPaddingLeft() + mMarginLeft + mPaddingLeft;
        int left = defaultLeft;

        int gap;
        int right;

        for (Span span = first; null != span; ) {
            List<View> views = span.views;
            if (views.isEmpty())
                break;
            if (span.reSpacing) {
                gap = span.totalSpacing / Math.max(views.size() + 1, 2);
            } else {
                gap = hGap;
            }
            bottom = top + span.maxViewHeight;
            for (View view : views) {
                left += gap;
                right = left + orientationHelper.getDecoratedMeasurementInOther(view);
                layoutChild(view, left, top, right, bottom, helper);
                left = right;
                handleStateOnResult(result, view);
            }

            top += span.maxViewHeight + vGap;
            left = defaultLeft;
            span = span.next;
        }

        result.mConsumed = bottom;
    }

    @Override
    public int computeAlignOffset(int offset, boolean isLayoutEnd, boolean useAnchor, LayoutManagerHelper helper) {
        final boolean layoutInVertical = helper.getOrientation() == VERTICAL;
        if (isLayoutEnd) {
            if (offset == getItemCount() - 1) {
                return layoutInVertical ? mMarginBottom + mPaddingBottom : mMarginRight + mPaddingRight;
            }
        } else {
            if (offset == 0) {
                return layoutInVertical ? -mMarginTop - mPaddingTop : -mMarginLeft - mPaddingLeft;
            }
        }

        return super.computeAlignOffset(offset, isLayoutEnd, useAnchor, helper);
    }

    private static class Span {
        int totalSpacing;
        int usedWidth;
        int maxViewHeight;
        boolean reSpacing;
        List<View> views = new ArrayList<>();

        Span next;

        public void clear() {
            totalSpacing = 0;
            usedWidth = 0;
            maxViewHeight = 0;
            reSpacing = false;
            views.clear();

            if (null != next) {
                next.clear();
            }
            next = null;
        }
    }
}
