package com.alibaba.android.vlayout.layout;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
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

    private final RowSpan first = new RowSpan();
    private boolean avgRowSpacing;

    public void setAvgRowSpacing(boolean enable) {
        this.avgRowSpacing = enable;
    }


    @Override
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state,
                            VirtualLayoutManager.LayoutStateWrapper layoutState,
                            LayoutChunkResult result, LayoutManagerHelper helper) {
        // reach the end of this layout
        if (isOutOfRange(layoutState.getCurrentPosition())) {
            return;
        }

        final int maxSize = helper.getContentWidth() - helper.getPaddingRight() - helper.getPaddingLeft()
                    - getHorizontalMargin() - getHorizontalPadding();


        int hGap = this.mHGap;
        int vGap = this.mVGap;

        OrientationHelperEx orientationHelper = helper.getMainOrientationHelper();

        RowSpan span = first;
        span.resetAll();
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
                span.totalRowSpacing = Math.max(maxSize - span.usedWidth, 0);
                span = span.moveToNextRow();
            }
            span.maxRowHeight = Math.max(span.maxRowHeight,
                    orientationHelper.getDecoratedMeasurement(view));
            span.usedWidth += hGap + viewWidth;
            span.views.add(view);
        }
        // add last line
        span.totalRowSpacing = maxSize - span.usedWidth;

        span.destroyAfterIfNeed();
        layoutVerticalChild(helper, orientationHelper, result, layoutState, hGap, vGap);
    }

    private void layoutVerticalChild(LayoutManagerHelper helper, OrientationHelperEx orientationHelper,
                                     LayoutChunkResult result, VirtualLayoutManager.LayoutStateWrapper layoutState, int hGap, int vGap) {
        final boolean avgRowSpacing = this.avgRowSpacing;
        final boolean layoutStart = layoutState.getLayoutDirection()
                == VirtualLayoutManager.LayoutStateWrapper.LAYOUT_START;

        final int defaultLeft = helper.getPaddingLeft() + mMarginLeft + mPaddingLeft;
        int left = defaultLeft;

        int top = 0;
        int bottom = 0;

        int avgHGap;
        int right;


        if (layoutStart) {
            bottom = layoutState.getOffset();
            result.mConsumed = bottom + mPaddingBottom + mMarginBottom;

            RowSpan span = first;
            while (null != span) {
                List<View> columnViews = span.views;
                if (columnViews.isEmpty()) {
                    break;
                }
                if (avgRowSpacing) {
                    avgHGap = span.totalRowSpacing / Math.max(columnViews.size() + 1, 2);
                } else {
                    avgHGap = hGap;
                }
                top = bottom - span.maxRowHeight;
                for (View columnView : columnViews) {
                    right = left + orientationHelper.getDecoratedMeasurementInOther(columnView);
                    layoutChild(columnView, left, top, right, bottom, helper);
                    left = right + avgHGap;
                    handleStateOnResult(result, columnView);
                }
                bottom = top - vGap;
                left = defaultLeft;
                span = span.next;
            }

        } else {
            top = layoutState.getOffset() + mMarginTop + mPaddingTop;

            RowSpan span = first;
            while (null != span) {
                List<View> columnViews = span.views;
                if (columnViews.isEmpty()) {
                    break;
                }
                if (avgRowSpacing) {
                    avgHGap = span.totalRowSpacing / Math.max(columnViews.size() + 1, 2);
                } else {
                    avgHGap = hGap;
                }

                bottom = top + span.maxRowHeight;

                for (View columnView : columnViews) {
                    right = left + orientationHelper.getDecoratedMeasurementInOther(columnView);
                    layoutChild(columnView, left, top, right, bottom, helper);
                    left = right + avgHGap;
                    handleStateOnResult(result, columnView);
                }
                top = bottom + vGap;
                left = defaultLeft;
                span = span.next;
            }


            result.mConsumed = bottom;
        }


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

    private static class RowSpan {
        int totalRowSpacing;
        int usedWidth;
        int maxRowHeight;
        List<View> views;

        RowSpan next;

        RowSpan() {
            this.views = new ArrayList<>();
        }

        void resetAll() {
            totalRowSpacing = 0;
            usedWidth = 0;
            maxRowHeight = 0;
            views.clear();
            if (null != next) {
                next.resetAll();
            }
            next = null;
        }

        RowSpan moveToNextRow() {
            RowSpan next = this.next;
            if (null == next) {
                this.next = next = new RowSpan();
            }
            return next;
        }

        void destroyAfterIfNeed() {
            RowSpan next = this.next;
            this.next = null;
            if (null != next) {
                next.views = null;
                next.destroyAfterIfNeed();
            }
        }
    }
}
