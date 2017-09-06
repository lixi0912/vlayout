package com.alibaba.android.vlayout.extend;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Arrays;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2017/9/5
 */

public class TypeDividerDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;
    private final Rect mBounds = new Rect();

    @NonNull
    private final TypeCondition condition;

    private int marginStart;
    private int marginEnd;
    private Drawable mDivider;

    public static TypeDividerDecoration simple(Context context, int orientation) {
        return new TypeDividerDecoration(context, orientation, new SimpleTypeCondition());
    }

    public static TypeDividerDecoration multiAble(Context context, int orientation) {
        return new TypeDividerDecoration(context, orientation, new MultiTypeCondition());
    }

    public TypeDividerDecoration(Context context, int orientation, @NonNull TypeCondition condition) {
        this.condition = condition;

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.mDivider = a.getDrawable(0);
        a.recycle();

        setOrientation(orientation);
    }

    public void registerType(int... types) {
        condition.registerType(types);
    }

    public void setMarginStart(int marginStart) {
        this.marginStart = marginStart;
    }

    public void setMarginEnd(int marginEnd) {
        this.marginEnd = marginEnd;
    }

    /**
     * Sets the orientation for this divider. This should be called if
     * {@link RecyclerView.LayoutManager} changes orientation.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        Drawable divider = mDivider;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (condition.typeWasRegistered(parent, child)
                    && condition.isSameType(parent, child, i)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                final int top = bottom - divider.getIntrinsicHeight();
                divider.setBounds(left, top + marginStart, right, bottom - marginEnd);
                divider.draw(canvas);
            }

        }
        canvas.restore();
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        Drawable divider = mDivider;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            if (condition.typeWasRegistered(parent, child)
                    && condition.isSameType(parent, child, i)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                final int top = bottom - divider.getIntrinsicHeight();
                divider.setBounds(left + marginStart, top, right - marginEnd, bottom);
                divider.draw(canvas);
            }
        }
        canvas.restore();
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        Drawable divider = mDivider;
        if (divider == null || !condition.typeWasRegistered(parent, view)) {
            outRect.setEmpty();
            return;
        }

        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
        }
    }


    private interface TypeCondition {
        void registerType(int[] types);


        boolean typeWasRegistered(RecyclerView parent, View child);

        /**
         * @param parent the RecyclerView
         * @param child  the view to draw divider
         * @return true if hasConsistItemType or has same type with next item
         */
        boolean isSameType(RecyclerView parent, View child, int index);
    }


    private static final class SimpleTypeCondition implements TypeCondition {

        private int[] types;

        @Override
        public void registerType(int[] types) {
            this.types = types;
            Arrays.sort(types);
        }

        @Override
        public boolean typeWasRegistered(RecyclerView parent, View child) {
            int position = parent.getChildAdapterPosition(child);
            int viewType = parent.getAdapter().getItemViewType(position);
            int result = Arrays.binarySearch(this.types, viewType);
            return result >= 0;
        }

        @Override
        public boolean isSameType(RecyclerView parent, View child, int index) {
            return true;
        }
    }

    private static final class MultiTypeCondition implements TypeCondition {
        @NonNull
        private final SparseArrayCompat<int[]> typeArray = new SparseArrayCompat<>();

        @Override
        public void registerType(int[] types) {
            SparseArrayCompat<int[]> typeArray = this.typeArray;
            typeArray.put(typeArray.size() + 1, types);
            Arrays.sort(types);
        }

        @Override
        public boolean typeWasRegistered(RecyclerView parent, View child) {
            int position = parent.getChildAdapterPosition(child);
            int viewType = parent.getAdapter().getItemViewType(position);
            SparseArrayCompat<int[]> typeArray = this.typeArray;
            int result;
            int typeSize = typeArray.size();
            for (int i = 0; i < typeSize; i++) {
                int[] types = typeArray.valueAt(i);
                result = Arrays.binarySearch(types, viewType);
                if (result >= 0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isSameType(RecyclerView parent, View child, int index) {
            SparseArrayCompat<int[]> typeArray = this.typeArray;
            final int childCount = parent.getChildCount();
            if (index == childCount - 1)
                return true;
            int position = parent.getChildAdapterPosition(child);

            int viewType = parent.getAdapter().getItemViewType(position);
            int nextType = parent.getAdapter().getItemViewType(position + 1);
            if (viewType == nextType)
                return true;

            int typeSize = typeArray.size();
            int result;
            for (int i = 0; i < typeSize; i++) {
                int[] types = typeArray.valueAt(i);
                result = Arrays.binarySearch(types, viewType);
                if (result >= 0) {
                    result = Arrays.binarySearch(types, nextType);
                    return result >= 0;
                }
            }
            return false;
        }
    }

}
