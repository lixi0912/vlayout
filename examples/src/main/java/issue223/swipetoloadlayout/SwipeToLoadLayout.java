package issue223.swipetoloadlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.alibaba.android.vlayout.example.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Aspsine on 2015/8/13.
 */
public class SwipeToLoadLayout extends FrameLayout {


    private static final String TAG = SwipeToLoadLayout.class.getSimpleName();

    private static final int DEFAULT_REFRESH_DONE_DURATION = 200;

    private static final int DEFAULT_REFRESH_RELEASE_DURATION = 200;


    private static final int DEFAULT_REFRESH_COMPLETE_DURATION = 500;

    private static final int DEFAULT_REFRESH_BEGINNING_DURATION = 500;

    private static final int DEFAULT_SWIPING_TO_LOAD_MORE_TO_DEFAULT_SCROLLING_DURATION = 200;

    private static final int DEFAULT_LOAD_MORE_RELEASE_DURATION = 200;


    private static final int DEFAULT_LOAD_MORE_COMPLETE_TO_DEFAULT_SCROLLING_DURATION = 300;

    private static final int DEFAULT_ANIMATE_TO_LOAD_MORE_DURATION = 300;

    /**
     * how hard to drag
     */
    private static final float DEFAULT_DRAG_RATIO = 0.5f;

    private static final int INVALID_POINTER = -1;

    private static final int INVALID_COORDINATE = -1;

    private AutoScroller mAutoScroller;

    private OnRefreshListener mRefreshListener;

    private OnLoadMoreListener mLoadMoreListener;

    @Nullable
    private View mHeaderView;

    @Nullable
    private View mTargetView;

    @Nullable
    private View mFooterView;

    private int mHeaderHeight;

    private int mFooterHeight;

    /**
     * indicate whether in debug mode
     */
    private boolean mDebug;

    private float mDragRatio = DEFAULT_DRAG_RATIO;

    private boolean mAutoLoading;

    /**
     * the threshold of the touch event
     */
    private final int mTouchSlop;

    /**
     * status of SwipeToLoadLayout
     */
    private int mStatus = STATUS.STATUS_DEFAULT;

    /**
     * target view top offset
     */
    private int mHeaderOffset;

    /**
     * target offset
     */
    private int mTargetOffset;

    /**
     * target view bottom offset
     */
    private int mFooterOffset;

    /**
     * init touch action down point.y
     */
    private float mInitDownY;

    /**
     * init touch action down point.x
     */
    private float mInitDownX;

    /**
     * last touch point.y
     */
    private float mLastY;

    /**
     * last touch point.x
     */
    private float mLastX;

    /**
     * action touch pointer's id
     */
    private int mActivePointerId;

    /**
     * <b>ATTRIBUTE:</b>
     * a switcher indicate whither refresh function is enabled
     */
    private boolean mRefreshEnabled = true;

    /**
     * <b>ATTRIBUTE:</b>
     * a switcher indicate whiter load more function is enabled
     */
    private boolean mLoadMoreEnabled = true;

    /**
     * <b>ATTRIBUTE:</b>
     * the style default classic
     */
    @STYLE
    private int mStyle = STYLE.CLASSIC;

    /**
     * <b>ATTRIBUTE:</b>
     * offset to trigger refresh
     */
    private float mRefreshTriggerOffset;

    /**
     * <b>ATTRIBUTE:</b>
     * offset to trigger load more
     */
    private float mLoadMoreTriggerOffset;

    /**
     * <b>ATTRIBUTE:</b>
     * the max value of top offset
     */
    private float mRefreshFinalDragOffset;

    /**
     * <b>ATTRIBUTE:</b>
     * the max value of bottom offset
     */
    private float mLoadMoreFinalDragOffset;

    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration swiping to refresh -> default
     */
    private int mAbortRefreshDuration = DEFAULT_REFRESH_DONE_DURATION;

    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration status release to refresh -> refreshing
     */
    private int mRefreshReleaseDuration = DEFAULT_REFRESH_RELEASE_DURATION;


    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration status refresh complete -> default
     * {@link #setRefreshing(boolean)} false
     */
    private int mRefreshCompleteDuration = DEFAULT_REFRESH_COMPLETE_DURATION;

    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration status default -> refreshing, mainly for auto refresh
     * {@link #setRefreshing(boolean)} true
     */
    private int mRefreshBeginningDuration = DEFAULT_REFRESH_BEGINNING_DURATION;

    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration status release to loading more -> loading more
     */
    private int mLoadMoreReleaseDuration = DEFAULT_LOAD_MORE_RELEASE_DURATION;


    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration status load more complete -> default
     * {@link #setLoadingMore(boolean)} false
     */
    private int mLoadMoreCompleteDuration = DEFAULT_LOAD_MORE_COMPLETE_TO_DEFAULT_SCROLLING_DURATION;

    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration swiping to load more -> default
     */
    private int mAbortLoadMoreDuration = DEFAULT_SWIPING_TO_LOAD_MORE_TO_DEFAULT_SCROLLING_DURATION;

    /**
     * <b>ATTRIBUTE:</b>
     * Scrolling duration status default -> loading more, mainly for auto load more
     * {@link #setLoadingMore(boolean)} true
     */
    private int mLoadMoreBeginningDuration = DEFAULT_ANIMATE_TO_LOAD_MORE_DURATION;


    /**
     * the style enum
     */
    @IntDef({STYLE.ABOVE, STYLE.BLEW, STYLE.CLASSIC, STYLE.SCALE})
    @Retention(RetentionPolicy.SOURCE)
    @interface STYLE {
        int CLASSIC = 0;
        int ABOVE = 1;
        int BLEW = 2;
        int SCALE = 3;
    }

    private int swipeTargetId;
    private int swipeHeaderId;
    private int swipeFooterId;

    @NonNull
    private ScrollAbleLookup scrollAbleLookup;

    public void setScrollAbleLookup(@NonNull ScrollAbleLookup lookup) {
        this.scrollAbleLookup = lookup;
        if (null != mTargetView) {
            lookup.onViewCaptured(this, mTargetView);
        }
    }

    public SwipeToLoadLayout(Context context) {
        this(context, null);
    }

    public SwipeToLoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeToLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeToLoadLayout, defStyleAttr, 0);
        try {

            int swipeStyle = STYLE.CLASSIC;
            float dragRatio = DEFAULT_DRAG_RATIO;
            int loadMoreDragOffset = 0;
            int loadMoreTriggerOffset = 0;
            int abortLoadMoreDuration = DEFAULT_SWIPING_TO_LOAD_MORE_TO_DEFAULT_SCROLLING_DURATION;
            int loadMoreBeginningDuration = DEFAULT_ANIMATE_TO_LOAD_MORE_DURATION;

            int loadMoreReleaseDuration = DEFAULT_LOAD_MORE_RELEASE_DURATION;
            int loadMoreCompleteDuration = DEFAULT_LOAD_MORE_COMPLETE_TO_DEFAULT_SCROLLING_DURATION;


            // load user-config-settings for loadMore
            boolean loadMore = a.getBoolean(R.styleable.SwipeToLoadLayout_load_more_enabled, true);

            dragRatio = a.getFloat(R.styleable.SwipeToLoadLayout_drag_ratio, dragRatio);
            swipeStyle = a.getInt(R.styleable.SwipeToLoadLayout_swipe_style, swipeStyle);
            loadMoreDragOffset = a.getDimensionPixelOffset(R.styleable.SwipeToLoadLayout_load_more_drag_offset, loadMoreDragOffset);
            loadMoreTriggerOffset = a.getDimensionPixelOffset(R.styleable.SwipeToLoadLayout_load_more_trigger_offset, loadMoreTriggerOffset);
            abortLoadMoreDuration = a.getInt(R.styleable.SwipeToLoadLayout_load_more_abort_duration, abortLoadMoreDuration);
            loadMoreReleaseDuration = a.getInt(R.styleable.SwipeToLoadLayout_load_more_release_duration, loadMoreReleaseDuration);
            loadMoreCompleteDuration = a.getInt(R.styleable.SwipeToLoadLayout_load_more_complete_duration, loadMoreCompleteDuration);

            loadMoreBeginningDuration = a.getInt(R.styleable.SwipeToLoadLayout_load_more_beginning_duration,
                    loadMoreBeginningDuration);


            // load user-config-settings for refresh

            int refreshDragOffset = 0;
            int refreshTriggerOffset = 0;
            int abortRefreshDuration = DEFAULT_REFRESH_DONE_DURATION;
            int refreshReleaseDuration = DEFAULT_REFRESH_RELEASE_DURATION;
            int refreshCompleteDuration = DEFAULT_REFRESH_COMPLETE_DURATION;
            int refreshBeginningDuration = DEFAULT_REFRESH_BEGINNING_DURATION;

            boolean refresh = a.getBoolean(R.styleable.SwipeToLoadLayout_refresh_enabled, true);
            refreshDragOffset = a.getDimensionPixelOffset(R.styleable.SwipeToLoadLayout_refresh_drag_offset,
                    refreshDragOffset);
            refreshTriggerOffset = a.getDimensionPixelOffset(R.styleable.SwipeToLoadLayout_refresh_trigger_offset,
                    refreshTriggerOffset);

            abortRefreshDuration = a.getInt(R.styleable.SwipeToLoadLayout_refresh_abort_duration
                    , abortRefreshDuration);

            refreshReleaseDuration = a.getInt(R.styleable.SwipeToLoadLayout_refresh_release_duration,
                    refreshReleaseDuration);


            refreshCompleteDuration = a.getInt(R.styleable.SwipeToLoadLayout_refresh_complete_duration,
                    refreshCompleteDuration);

            refreshBeginningDuration =
                    a.getInt(R.styleable.SwipeToLoadLayout_refresh_beginning_duration, refreshBeginningDuration);


            // update settings
            // noinspection WrongConstant
            setSwipeStyle(swipeStyle);
            setDragRatio(dragRatio);

            // update settings for loadMoreConfigs
            setLoadMoreEnabled(loadMore);
            setLoadMoreFinalDragOffset(loadMoreDragOffset);
            setLoadMoreTriggerOffset(loadMoreTriggerOffset);

            setLoadMoreBeginningDuration(loadMoreBeginningDuration);
            setAbortLoadMoreDuration(abortLoadMoreDuration);
            setLoadMoreReleaseDuration(loadMoreReleaseDuration);

            setLoadMoreCompleteDuration(loadMoreCompleteDuration);


            // update settings for refreshConfigs
            setRefreshEnabled(refresh);
            setRefreshFinalDragOffset(refreshDragOffset);
            setRefreshTriggerOffset(refreshTriggerOffset);
            setAbortRefreshDuration(abortRefreshDuration);
            setRefreshReleaseDuration(refreshReleaseDuration);

            setRefreshCompleteDuration(refreshCompleteDuration);
            setRefreshBeginningDuration(refreshBeginningDuration);
            this.swipeHeaderId = a.getResourceId(R.styleable.SwipeToLoadLayout_swipe_refresh_header, 0);
            this.swipeTargetId = a.getResourceId(R.styleable.SwipeToLoadLayout_swipe_target, 0);
            this.swipeFooterId = a.getResourceId(R.styleable.SwipeToLoadLayout_swipe_load_more_footer, 0);

            setScrollAbleLookup(new ScrollAbleLookup.Default());
        } finally {
            a.recycle();
        }

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mAutoScroller = new AutoScroller();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View headView = findViewById(swipeHeaderId);
        View targetView = findViewById(swipeTargetId);
        View footerView = findViewById(swipeFooterId);

        this.mTargetView = targetView;
        this.scrollAbleLookup.onViewCaptured(this, targetView);

        if (headView instanceof SwipeRefreshTrigger) {
            headView.setVisibility(GONE);
            mHeaderView = headView;
        }
        if (footerView instanceof SwipeTrigger) {
            footerView.setVisibility(GONE);
            mFooterView = footerView;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // header
        if (mHeaderView != null) {
            final View headerView = mHeaderView;
            measureChildWithMargins(headerView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = ((MarginLayoutParams) headerView.getLayoutParams());
            mHeaderHeight = headerView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (mRefreshTriggerOffset < mHeaderHeight) {
                mRefreshTriggerOffset = mHeaderHeight;
            }
        }
        // target
        if (mTargetView != null) {
            final View targetView = mTargetView;
            measureChildWithMargins(targetView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        // footer
        if (mFooterView != null) {
            final View footerView = mFooterView;
            measureChildWithMargins(footerView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = ((MarginLayoutParams) footerView.getLayoutParams());
            mFooterHeight = footerView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (mLoadMoreTriggerOffset < mFooterHeight) {
                mLoadMoreTriggerOffset = mFooterHeight;
            }
        }


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // swipeToRefresh -> finger up -> finger down if the status is still swipeToRefresh
                // in onInterceptTouchEvent ACTION_DOWN event will stop the scroller
                // if the event pass to the child view while ACTION_MOVE(condition is false)
                // in onInterceptTouchEvent ACTION_MOVE the ACTION_UP or ACTION_CANCEL will not be
                // passed to onInterceptTouchEvent and onTouchEvent. Instead It will be passed to
                // child view's onTouchEvent. So we must deal this situation in dispatchTouchEvent
                onActivePointerUp();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                mInitDownY = mLastY = getMotionEventY(event, mActivePointerId);
                mInitDownX = mLastX = getMotionEventX(event, mActivePointerId);

                // if it isn't an ing status or default status
                if (STATUS.isSwipingToRefresh(mStatus) || STATUS.isSwipingToLoadMore(mStatus) ||
                        STATUS.isReleaseToRefresh(mStatus) || STATUS.isReleaseToLoadMore(mStatus)) {
                    // abort autoScrolling, not trigger the method #autoScrollFinished()
                    mAutoScroller.abortIfRunning();
                    if (mDebug) {
                        Log.i(TAG, "Another finger down, abort auto scrolling, let the new finger handle");
                    }
                }

                if (STATUS.isSwipingToRefresh(mStatus) || STATUS.isReleaseToRefresh(mStatus)
                        || STATUS.isSwipingToLoadMore(mStatus) || STATUS.isReleaseToLoadMore(mStatus)) {
                    return true;
                }

                // let children view handle the ACTION_DOWN;

                // 1. children consumed:
                // if at least one of children onTouchEvent() ACTION_DOWN return true.
                // ACTION_DOWN event will not return to SwipeToLoadLayout#onTouchEvent().
                // but the others action can be handled by SwipeToLoadLayout#onInterceptTouchEvent()

                // 2. children not consumed:
                // if children onTouchEvent() ACTION_DOWN return false.
                // ACTION_DOWN event will return to SwipeToLoadLayout's onTouchEvent().
                // SwipeToLoadLayout#onTouchEvent() ACTION_DOWN return true to consume the ACTION_DOWN event.

                // anyway: handle action down in onInterceptTouchEvent() to init is an good option
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                float y = getMotionEventY(event, mActivePointerId);
                float x = getMotionEventX(event, mActivePointerId);
                final float yInitDiff = y - mInitDownY;
                final float xInitDiff = x - mInitDownX;
                mLastY = y;
                mLastX = x;
                boolean moved = Math.abs(yInitDiff) > Math.abs(xInitDiff)
                        && Math.abs(yInitDiff) > mTouchSlop;
                boolean triggerCondition =
                        // refresh trigger condition
                        (yInitDiff > 0 && moved && onCheckCanRefresh()) ||
                                //load more trigger condition
                                (yInitDiff < 0 && moved && onCheckCanLoadMore());
                if (triggerCondition) {
                    // if the refresh's or load more's trigger condition  is true,
                    // intercept the move action event and pass it to SwipeToLoadLayout#onTouchEvent()
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
                mInitDownY = mLastY = getMotionEventY(event, mActivePointerId);
                mInitDownX = mLastX = getMotionEventX(event, mActivePointerId);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                return true;

            case MotionEvent.ACTION_MOVE:
                // take over the ACTION_MOVE event from SwipeToLoadLayout#onInterceptTouchEvent()
                // if condition is true
                final float y = getMotionEventY(event, mActivePointerId);
                final float x = getMotionEventX(event, mActivePointerId);

                final float yDiff = y - mLastY;
                final float xDiff = x - mLastX;
                mLastY = y;
                mLastX = x;

                if (Math.abs(xDiff) > Math.abs(yDiff) && Math.abs(xDiff) > mTouchSlop) {
                    return false;
                }

                if (STATUS.isStatusDefault(mStatus)) {
                    if (yDiff > 0 && onCheckCanRefresh()) {
                        mRefreshCallback.onPrepare(this, STATUS.STATUS_SWIPING_TO_REFRESH);
                        setStatus(STATUS.STATUS_SWIPING_TO_REFRESH);
                    } else if (yDiff < 0 && onCheckCanLoadMore()) {
                        mLoadMoreCallback.onPrepare(this, STATUS.STATUS_SWIPING_TO_LOAD_MORE);
                        setStatus(STATUS.STATUS_SWIPING_TO_LOAD_MORE);
                    }
                } else if (STATUS.isRefreshStatus(mStatus)) {
                    if (mTargetOffset <= 0) {
                        setStatus(STATUS.STATUS_DEFAULT);
                        fixCurrentStatusLayout();
                        mRefreshCallback.onReset();
                        return false;
                    }
                } else if (STATUS.isLoadMoreStatus(mStatus)) {
                    if (mTargetOffset >= 0) {
                        setStatus(STATUS.STATUS_DEFAULT);
                        fixCurrentStatusLayout();
                        mLoadMoreCallback.onReset();
                        return false;
                    }
                }

                if (STATUS.isRefreshStatus(mStatus)) {
                    if (STATUS.isSwipingToRefresh(mStatus) || STATUS.isReleaseToRefresh(mStatus)) {
                        if (mTargetOffset >= mRefreshTriggerOffset) {
                            setStatus(STATUS.STATUS_RELEASE_TO_REFRESH);
                        } else {
                            setStatus(STATUS.STATUS_SWIPING_TO_REFRESH);
                        }
                        fingerScroll(yDiff);
                    }
                } else if (STATUS.isLoadMoreStatus(mStatus)) {
                    if (STATUS.isSwipingToLoadMore(mStatus) || STATUS.isReleaseToLoadMore(mStatus)) {
                        if (-mTargetOffset >= mLoadMoreTriggerOffset) {
                            setStatus(STATUS.STATUS_RELEASE_TO_LOAD_MORE);
                        } else {
                            setStatus(STATUS.STATUS_SWIPING_TO_LOAD_MORE);
                        }
                        fingerScroll(yDiff);
                    }
                }
                return true;

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                if (pointerId != INVALID_POINTER) {
                    mActivePointerId = pointerId;
                }
                mInitDownY = mLastY = getMotionEventY(event, mActivePointerId);
                mInitDownX = mLastX = getMotionEventX(event, mActivePointerId);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
                mInitDownY = mLastY = getMotionEventY(event, mActivePointerId);
                mInitDownX = mLastX = getMotionEventX(event, mActivePointerId);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                mActivePointerId = INVALID_POINTER;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * set debug mode(default value false)
     *
     * @param debug if true log on, false log off
     */
    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    /**
     * is refresh function is enabled
     */
    public boolean isRefreshEnabled() {
        return mRefreshEnabled && null != mHeaderView;
    }

    /**
     * switch refresh function on or off
     */
    public void setRefreshEnabled(boolean enable) {
        this.mRefreshEnabled = enable;
    }

    /**
     * is load more function is enabled
     */
    public boolean isLoadMoreEnabled() {
        return mLoadMoreEnabled && null != mFooterView;
    }

    /**
     * switch load more function on or off
     */
    public void setLoadMoreEnabled(boolean enable) {
        this.mLoadMoreEnabled = enable;
    }

    /**
     * is current status refreshing
     */
    public boolean isRefreshing() {
        return STATUS.isRefreshing(mStatus);
    }

    /**
     * is current status loading more
     */
    public boolean isLoadingMore() {
        return STATUS.isLoadingMore(mStatus);
    }

    /**
     * set refresh header view, the view must at lease be an implement of {@code SwipeRefreshTrigger}.
     * the view can also implement {@code SwipeTrigger} for more extension functions
     *
     * @param view
     */
    public <V extends View & SwipeRefreshTrigger> void setRefreshHeaderView(V view) {
        View preView = this.mHeaderView;
        if (preView != view) {
            if (null != preView) {
                removeView(preView);
            }
            if (null != view) {
                addView(view);
            }
            this.mHeaderView = view;
        }
    }

    /**
     * set load more footer view, the view must at least be an implement of SwipeLoadTrigger
     * the view can also implement {@code SwipeTrigger} for more extension functions
     */
    public <V extends View & SwipeLoadMoreTrigger> void setLoadMoreFooterView(V view) {
        View preView = this.mFooterView;
        if (preView != view) {
            if (null != preView) {
                removeView(preView);
            }
            if (null != view) {
                addView(view);
            }
            this.mFooterView = view;
        }
    }

    /**
     * set the style of the refresh header
     */
    public void setSwipeStyle(@STYLE int style) {
        if (this.mStyle != style) {
            this.mStyle = style;
            requestLayout();
        }
    }

    /**
     * set how hard to drag. bigger easier, smaller harder;
     *
     * @param dragRatio default value is {@link #DEFAULT_DRAG_RATIO}
     */
    public void setDragRatio(float dragRatio) {
        this.mDragRatio = dragRatio;
    }

    /**
     * set the value of {@link #mRefreshTriggerOffset}.
     * Default value is the refresh header view height {@link #mHeaderHeight}<p/>
     * If the offset you set is smaller than {@link #mHeaderHeight} or not set,
     * using {@link #mHeaderHeight} as default value
     */
    public void setRefreshTriggerOffset(int offset) {
        mRefreshTriggerOffset = offset;
    }

    /**
     * set the value of {@link #mLoadMoreTriggerOffset}.
     * Default value is the load more footer view height {@link #mFooterHeight}<p/>
     * If the offset you set is smaller than {@link #mFooterHeight} or not set,
     * using {@link #mFooterHeight} as default value
     */
    public void setLoadMoreTriggerOffset(int offset) {
        mLoadMoreTriggerOffset = offset;
    }

    /**
     * Set the final offset you can swipe to refresh.<br/>
     * If the offset you set is 0(default value) or smaller than {@link #mRefreshTriggerOffset}
     * there no final offset
     */
    public void setRefreshFinalDragOffset(int offset) {
        mRefreshFinalDragOffset = offset;
    }

    /**
     * Set the final offset you can swipe to load more.<br/>
     * If the offset you set is 0(default value) or smaller than {@link #mLoadMoreTriggerOffset},
     * there no final offset
     */
    public void setLoadMoreFinalDragOffset(int offset) {
        mLoadMoreFinalDragOffset = offset;
    }

    /**
     * set {@link #mAbortRefreshDuration} in milliseconds
     */
    public void setAbortRefreshDuration(int duration) {
        this.mAbortRefreshDuration = duration;
    }

    /**
     * set {@link #mRefreshReleaseDuration} in milliseconds
     */
    public void setRefreshReleaseDuration(int duration) {
        this.mRefreshReleaseDuration = duration;
    }


    /**
     * set {@link #mRefreshCompleteDuration} in milliseconds
     */
    public void setRefreshCompleteDuration(int duration) {
        this.mRefreshCompleteDuration = duration;
    }

    /**
     * set {@link #mRefreshBeginningDuration} in milliseconds
     */
    public void setRefreshBeginningDuration(int duration) {
        this.mRefreshBeginningDuration = duration;
    }

    /**
     * set {@link @mAbortLoadMoreDuration} in milliseconds
     */
    public void setAbortLoadMoreDuration(int duration) {
        this.mAbortLoadMoreDuration = duration;
    }

    /**
     * set {@link #mLoadMoreReleaseDuration} in milliseconds
     */
    public void setLoadMoreReleaseDuration(int duration) {
        this.mLoadMoreReleaseDuration = duration;
    }


    /**
     * set {@link #mLoadMoreCompleteDuration} in milliseconds
     */
    public void setLoadMoreCompleteDuration(int duration) {
        this.mLoadMoreCompleteDuration = duration;
    }

    /**
     * set {@link #mLoadMoreBeginningDuration} in milliseconds
     */
    public void setLoadMoreBeginningDuration(int duration) {
        this.mLoadMoreBeginningDuration = duration;
    }

    /**
     * set an {@link OnRefreshListener} to listening refresh event
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    /**
     * set an {@link OnLoadMoreListener} to listening load more event
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mLoadMoreListener = listener;
    }

    /**
     * auto refresh or cancel
     */
    public void setRefreshing(boolean refreshing) {
        if (!isRefreshEnabled() && !STATUS.isRefreshing(mStatus)) {
            return;
        }
        this.mAutoLoading = refreshing;
        if (refreshing) {
            if (STATUS.isStatusDefault(mStatus)) {
                setStatus(STATUS.STATUS_SWIPING_TO_REFRESH);
                refreshBeginning();
            }
        } else {
            if (STATUS.isRefreshing(mStatus)) {
                mRefreshCallback.onComplete();
                refreshComplete();
            }
        }
    }


    /**
     * auto loading more or cancel
     */
    public void setLoadingMore(boolean loadingMore) {
        if (!isLoadMoreEnabled() && !STATUS.isLoadingMore(mStatus)) {
            return;
        }
        this.mAutoLoading = loadingMore;
        if (loadingMore) {
            if (STATUS.isStatusDefault(mStatus)) {
                setStatus(STATUS.STATUS_SWIPING_TO_LOAD_MORE);
                loadMoreBeginning();
            }
        } else {
            if (STATUS.isLoadingMore(mStatus)) {
                mLoadMoreCallback.onComplete();
                loadMoreComplete();
            }
        }
    }

    static final int CHILD_SCROLL_UP = -1;
    static final int CHILD_SCROLL_DOWN = 1;

    /**
     * copy from {@link android.support.v4.widget.SwipeRefreshLayout#canChildScrollUp()}
     *
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    protected boolean canChildScrollUp() {
        return scrollAbleLookup.canScrollVertically(this, mTargetView, CHILD_SCROLL_UP);
    }

    /**
     * Whether it is possible for the child view of this layout to
     * scroll down. Override this if the child view is a custom view.
     */
    protected boolean canChildScrollDown() {
        return scrollAbleLookup.canScrollVertically(this, mTargetView, CHILD_SCROLL_DOWN);
    }

    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;

    /**
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @see #onLayout(boolean, int, int, int, int)
     */
    private void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {


        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();


        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        // layout header
        final View headerView = mHeaderView;
        if (headerView != null && headerView.getVisibility() == VISIBLE) {
            MarginLayoutParams lp = (MarginLayoutParams) headerView.getLayoutParams();
            final int headerLeft = parentLeft + lp.leftMargin;
            final int headerTop;
            switch (mStyle) {
                case STYLE.CLASSIC:
                    // classic
                    headerTop = parentTop + lp.topMargin - mHeaderHeight + mHeaderOffset;
                    break;
                case STYLE.ABOVE:
                    // classic
                    headerTop = parentTop + lp.topMargin - mHeaderHeight + mHeaderOffset;
                    break;
                case STYLE.BLEW:
                    // blew
                    headerTop = parentTop + lp.topMargin;
                    break;
                case STYLE.SCALE:
                    // scale
                    headerTop = parentTop + lp.topMargin - mHeaderHeight / 2 + mHeaderOffset / 2;
                    break;
                default:
                    // classic
                    headerTop = parentTop + lp.topMargin - mHeaderHeight + mHeaderOffset;
                    break;
            }
            final int headerRight = headerLeft + headerView.getMeasuredWidth();
            final int headerBottom = headerTop + headerView.getMeasuredHeight();
            headerView.layout(headerLeft, headerTop, headerRight, headerBottom);
        }


        // layout target
        final View targetView = mTargetView;
        if (targetView != null && targetView.getVisibility() == View.VISIBLE) {
            MarginLayoutParams lp = (MarginLayoutParams) targetView.getLayoutParams();
            final int targetLeft = parentLeft + lp.leftMargin;
            final int targetTop;

            switch (mStyle) {
                case STYLE.CLASSIC:
                    // classic
                    targetTop = parentTop + lp.topMargin + mTargetOffset;
                    break;
                case STYLE.ABOVE:
                    // above
                    targetTop = parentTop + lp.topMargin;
                    break;
                case STYLE.BLEW:
                    // classic
                    targetTop = parentTop + lp.topMargin + mTargetOffset;
                    break;
                case STYLE.SCALE:
                    // classic
                    targetTop = parentTop + lp.topMargin + mTargetOffset;
                    break;
                default:
                    // classic
                    targetTop = parentTop + lp.topMargin + mTargetOffset;
                    break;
            }
            final int targetRight = targetLeft + targetView.getMeasuredWidth();
            final int targetBottom = targetTop + targetView.getMeasuredHeight();
            targetView.layout(targetLeft, targetTop, targetRight, targetBottom);
        }

        // layout footer
        final View footerView = mFooterView;
        if (footerView != null && footerView.getVisibility() == VISIBLE) {
            MarginLayoutParams lp = (MarginLayoutParams) footerView.getLayoutParams();
            final int footerLeft = parentLeft + lp.leftMargin;
            final int footerBottom;
            switch (mStyle) {
                case STYLE.CLASSIC:
                    // classic
                    footerBottom = parentBottom - lp.bottomMargin + mFooterHeight + mFooterOffset;
                    break;
                case STYLE.ABOVE:
                    // classic
                    footerBottom = parentBottom - lp.bottomMargin + mFooterHeight + mFooterOffset;
                    break;
                case STYLE.BLEW:
                    // blew
                    footerBottom = parentBottom - lp.bottomMargin;
                    break;
                case STYLE.SCALE:
                    // scale
                    footerBottom = parentBottom - lp.bottomMargin + mFooterHeight / 2 + mFooterOffset / 2;
                    break;
                default:
                    // classic
                    footerBottom = parentBottom - lp.bottomMargin + mFooterHeight + mFooterOffset;
                    break;
            }
            final int footerTop = footerBottom - footerView.getMeasuredHeight();
            final int footerRight = footerLeft + footerView.getMeasuredWidth();

            footerView.layout(footerLeft, footerTop, footerRight, footerBottom);
        }

        if (mStyle == STYLE.CLASSIC
                || mStyle == STYLE.ABOVE) {
            if (headerView != null) {
                headerView.bringToFront();
            }
            if (footerView != null) {
                footerView.bringToFront();
            }
        } else if (mStyle == STYLE.BLEW || mStyle == STYLE.SCALE) {
            if (targetView != null) {
                targetView.bringToFront();
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            int childID = child.getId();
            if (childID == swipeFooterId || childID == swipeHeaderId || childID == swipeTargetId) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            int childLeft;
            int childTop;

            int gravity = lp.gravity;
            if (gravity == -1) {
                gravity = DEFAULT_CHILD_GRAVITY;
            }

            final int layoutDirection = ViewCompat.getLayoutDirection(this);
            final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = parentLeft + (parentRight - parentLeft - childWidth) / 2 +
                            lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.RIGHT:
                    if (!forceLeftGravity) {
                        childLeft = parentRight - childWidth - lp.rightMargin;
                        break;
                    }
                case Gravity.LEFT:
                default:
                    childLeft = parentLeft + lp.leftMargin;
            }

            switch (verticalGravity) {
                case Gravity.TOP:
                    childTop = parentTop + lp.topMargin;
                    break;
                case Gravity.CENTER_VERTICAL:
                    childTop = parentTop + (parentBottom - parentTop - childHeight) / 2 +
                            lp.topMargin - lp.bottomMargin;
                    break;
                case Gravity.BOTTOM:
                    childTop = parentBottom - childHeight - lp.bottomMargin;
                    break;
                default:
                    childTop = parentTop + lp.topMargin;
            }

            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    private void fixCurrentStatusLayout() {

        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();

        if (STATUS.isRefreshing(mStatus)) {
            mTargetOffset = (int) (mRefreshTriggerOffset + 0.5f);
            mHeaderOffset = mTargetOffset;
            mFooterOffset = 0;
            layoutChildren(left, top, right, bottom, false);
            invalidate();
        } else if (STATUS.isStatusDefault(mStatus)) {
            mTargetOffset = 0;
            mHeaderOffset = 0;
            mFooterOffset = 0;
            layoutChildren(left, top, right, bottom, false);
            invalidate();
        } else if (STATUS.isLoadingMore(mStatus)) {
            mTargetOffset = -(int) (mLoadMoreTriggerOffset + 0.5f);
            mHeaderOffset = 0;
            mFooterOffset = mTargetOffset;
            layoutChildren(left, top, right, bottom, false);
            invalidate();
        }
    }

    /**
     * scrolling by physical touch with your fingers
     */
    private void fingerScroll(final float yDiff) {
        float ratio = mDragRatio;
        float yScrolled = yDiff * ratio;

        // make sure (targetOffset>0 -> targetOffset=0 -> default status)
        // or (targetOffset<0 -> targetOffset=0 -> default status)
        // forbidden fling (targetOffset>0 -> targetOffset=0 ->targetOffset<0 -> default status)
        // or (targetOffset<0 -> targetOffset=0 ->targetOffset>0 -> default status)
        // I am so smart :)

        float tmpTargetOffset = yScrolled + mTargetOffset;
        if ((tmpTargetOffset > 0 && mTargetOffset < 0)
                || (tmpTargetOffset < 0 && mTargetOffset > 0)) {
            yScrolled = -mTargetOffset;
        }


        if (mRefreshFinalDragOffset >= mRefreshTriggerOffset && tmpTargetOffset > mRefreshFinalDragOffset) {
            yScrolled = mRefreshFinalDragOffset - mTargetOffset;
        } else if (mLoadMoreFinalDragOffset >= mLoadMoreTriggerOffset && -tmpTargetOffset > mLoadMoreFinalDragOffset) {
            yScrolled = -mLoadMoreFinalDragOffset - mTargetOffset;
        }

        if (STATUS.isRefreshStatus(mStatus)) {
            mRefreshCallback.onMove(mTargetOffset, false, false);
        } else if (STATUS.isLoadMoreStatus(mStatus)) {
            mLoadMoreCallback.onMove(mTargetOffset, false, false);
        }
        updateScroll(yScrolled);
    }

    private void autoScroll(final float yScrolled) {

        if (STATUS.isSwipingToRefresh(mStatus)) {
            mRefreshCallback.onMove(mTargetOffset, false, true);
        } else if (STATUS.isReleaseToRefresh(mStatus)) {
            mRefreshCallback.onMove(mTargetOffset, false, true);
        } else if (STATUS.isRefreshing(mStatus)) {
            mRefreshCallback.onMove(mTargetOffset, true, true);
        } else if (STATUS.isSwipingToLoadMore(mStatus)) {
            mLoadMoreCallback.onMove(mTargetOffset, false, true);
        } else if (STATUS.isReleaseToLoadMore(mStatus)) {
            mLoadMoreCallback.onMove(mTargetOffset, false, true);
        } else if (STATUS.isLoadingMore(mStatus)) {
            mLoadMoreCallback.onMove(mTargetOffset, true, true);
        }
        updateScroll(yScrolled);
    }

    /**
     * Process the scrolling(auto or physical) and append the diff values to mTargetOffset
     * I think it's the most busy and core method. :) a ha ha ha ha...
     */
    private void updateScroll(final float yScrolled) {
        if (yScrolled == 0) {
            return;
        }
        mTargetOffset += yScrolled;

        if (STATUS.isRefreshStatus(mStatus)) {
            mHeaderOffset = mTargetOffset;
            mFooterOffset = 0;
        } else if (STATUS.isLoadMoreStatus(mStatus)) {
            mFooterOffset = mTargetOffset;
            mHeaderOffset = 0;
        }

        if (mDebug) {
            Log.i(TAG, "mTargetOffset = " + mTargetOffset);
        }
        layoutChildren(getLeft(), getTop(), getRight(), getBottom(), false);
        invalidate();
    }

    /**
     * on active finger up
     */
    private void onActivePointerUp() {
        if (STATUS.isSwipingToRefresh(mStatus)) {
            // simply return
            abortRefresh();
        } else if (STATUS.isSwipingToLoadMore(mStatus)) {
            // simply return
            abortLoadMore();
        } else if (STATUS.isReleaseToRefresh(mStatus)) {
            // return to header height and perform refresh
            mRefreshCallback.onRelease();
            refreshRelease();
        } else if (STATUS.isReleaseToLoadMore(mStatus)) {
            // return to footer height and perform loadMore
            mLoadMoreCallback.onRelease();
            loadMoreRelease();
        }
    }

    /**
     * on not active finger up
     */
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private void refreshBeginning() {
        mAutoScroller.autoScroll((int) (mRefreshTriggerOffset + 0.5f), mRefreshBeginningDuration);
    }

    private void abortRefresh() {
        mAutoScroller.autoScroll(-mHeaderOffset, mAbortRefreshDuration);
    }


    private void refreshRelease() {
        mAutoScroller.autoScroll(mHeaderHeight - mHeaderOffset, mRefreshReleaseDuration);
    }


    private void refreshComplete() {
        mAutoScroller.autoScroll(-mHeaderOffset, mRefreshCompleteDuration);
    }


    private void loadMoreBeginning() {
        mAutoScroller.autoScroll(-(int) (mLoadMoreTriggerOffset + 0.5f), mLoadMoreBeginningDuration);
    }

    private void abortLoadMore() {
        mAutoScroller.autoScroll(-mFooterOffset, mAbortLoadMoreDuration);
    }

    private void loadMoreRelease() {
        mAutoScroller.autoScroll(-mFooterOffset - mFooterHeight, mLoadMoreReleaseDuration);
    }

    private void loadMoreComplete() {
        mAutoScroller.autoScroll(-mFooterOffset, mLoadMoreCompleteDuration);
    }

    /**
     * invoke when {@link AutoScroller#finish()} is automatic
     */
    private void autoScrollFinished() {
        int mLastStatus = mStatus;

        if (STATUS.isReleaseToRefresh(mStatus)) {
            setStatus(STATUS.STATUS_REFRESHING);
            fixCurrentStatusLayout();
            mRefreshCallback.onRefresh();

        } else if (STATUS.isRefreshing(mStatus)) {
            setStatus(STATUS.STATUS_DEFAULT);
            fixCurrentStatusLayout();
            mRefreshCallback.onReset();

        } else if (STATUS.isSwipingToRefresh(mStatus)) {
            if (mAutoLoading) {
                mAutoLoading = false;
                setStatus(STATUS.STATUS_REFRESHING);
                fixCurrentStatusLayout();
                mRefreshCallback.onRefresh();
            } else {
                setStatus(STATUS.STATUS_DEFAULT);
                fixCurrentStatusLayout();
                mRefreshCallback.onReset();
            }
        } else if (STATUS.isStatusDefault(mStatus)) {

        } else if (STATUS.isSwipingToLoadMore(mStatus)) {
            if (mAutoLoading) {
                mAutoLoading = false;
                setStatus(STATUS.STATUS_LOADING_MORE);
                fixCurrentStatusLayout();
                mLoadMoreCallback.onLoadMore();
            } else {
                setStatus(STATUS.STATUS_DEFAULT);
                fixCurrentStatusLayout();
                mLoadMoreCallback.onReset();
            }
        } else if (STATUS.isLoadingMore(mStatus)) {
            setStatus(STATUS.STATUS_DEFAULT);
            fixCurrentStatusLayout();
            mLoadMoreCallback.onReset();
        } else if (STATUS.isReleaseToLoadMore(mStatus)) {
            setStatus(STATUS.STATUS_LOADING_MORE);
            fixCurrentStatusLayout();
            mLoadMoreCallback.onLoadMore();
        } else {
            throw new IllegalStateException("illegal state: " + STATUS.getStatus(mStatus));
        }

        if (mDebug) {
            Log.i(TAG, STATUS.getStatus(mLastStatus) + " -> " + STATUS.getStatus(mStatus));
        }
    }

    /**
     * check if it can refresh
     */
    private boolean onCheckCanRefresh() {
        return isRefreshEnabled() && !canChildScrollUp() && mRefreshTriggerOffset > 0;
    }

    /**
     * check if it can load more
     */
    private boolean onCheckCanLoadMore() {
        return isLoadMoreEnabled() && !canChildScrollDown()
                && mLoadMoreTriggerOffset > 0
                && mLoadMoreCallback.canLoadMore();
    }

    private float getMotionEventY(MotionEvent event, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(event, activePointerId);
        if (index < 0) {
            return INVALID_COORDINATE;
        }
        return MotionEventCompat.getY(event, index);
    }

    private float getMotionEventX(MotionEvent event, int activePointId) {
        final int index = MotionEventCompat.findPointerIndex(event, activePointId);
        if (index < 0) {
            return INVALID_COORDINATE;
        }
        return MotionEventCompat.getX(event, index);
    }

    private final SwipeRefreshTrigger mRefreshCallback = new SwipeRefreshTrigger() {
        @Override
        public void onPrepare(SwipeToLoadLayout layout, int state) {
            if (isRefreshEnabled() && STATUS.isStatusDefault(mStatus)) {
                View headView = mHeaderView;
                assert headView != null;

                headView.setVisibility(VISIBLE);
                ((SwipeTrigger) headView).onPrepare(layout, state);
            }
        }

        @Override
        public void onMove(int y, boolean isComplete, boolean automatic) {
            if (isRefreshEnabled() && STATUS.isRefreshStatus(mStatus)) {
                View headView = mHeaderView;
                assert headView != null;

                headView.setVisibility(VISIBLE);
                ((SwipeTrigger) headView).onMove(y, isComplete, automatic);
            }
        }

        @Override
        public void onRelease() {
            if (isRefreshEnabled() && STATUS.isReleaseToRefresh(mStatus)) {
                View headView = mHeaderView;
                assert headView != null;
                ((SwipeTrigger) headView).onRelease();
            }
        }

        @Override
        public void onRefresh() {
            if (isRefreshEnabled() && STATUS.isRefreshing(mStatus)) {
                View headView = mHeaderView;
                assert headView != null;
                ((SwipeRefreshTrigger) headView).onRefresh();

                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
            }
        }

        @Override
        public void onComplete() {
            if (isRefreshEnabled()) {
                View headView = mHeaderView;
                assert headView != null;
                ((SwipeTrigger) headView).onComplete();
            }
        }

        @Override
        public void onReset() {
            if (isRefreshEnabled() && STATUS.isStatusDefault(mStatus)) {
                View headView = mHeaderView;
                assert headView != null;

                ((SwipeTrigger) headView).onReset();
                headView.setVisibility(GONE);
            }
        }
    };

    private final SwipeLoadMoreTrigger mLoadMoreCallback = new SwipeLoadMoreTrigger() {

        @Override
        public void onPrepare(SwipeToLoadLayout layout, int state) {
            if (isLoadMoreEnabled() && STATUS.isStatusDefault(mStatus)) {
                View footView = mFooterView;
                assert footView != null;
                footView.setVisibility(VISIBLE);
                ((SwipeTrigger) footView).onPrepare(layout, state);
            }
        }

        @Override
        public void onMove(int y, boolean isComplete, boolean automatic) {
            if (isLoadMoreEnabled() && STATUS.isLoadMoreStatus(mStatus)) {
                View footView = mFooterView;
                assert footView != null;
                footView.setVisibility(VISIBLE);
                ((SwipeTrigger) footView).onMove(y, isComplete, automatic);
            }
        }

        @Override
        public void onRelease() {
            if (isLoadMoreEnabled() && STATUS.isReleaseToLoadMore(mStatus)) {
                View footView = mFooterView;
                assert footView != null;
                ((SwipeTrigger) footView).onRelease();
            }
        }

        @Override
        public void onLoadMore() {
            if (isLoadMoreEnabled() && STATUS.isLoadingMore(mStatus)) {
                View footView = mFooterView;
                assert footView != null;

                ((SwipeLoadMoreTrigger) footView).onLoadMore();
                if (mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                }
            }
        }

        @Override
        public boolean canLoadMore() {
            if (isLoadMoreEnabled()) {
                assert mFooterView != null;
                return ((SwipeLoadMoreTrigger) mFooterView).canLoadMore();
            }
            return false;
        }

        @Override
        public void onComplete() {
            if (isLoadMoreEnabled()) {
                View footView = mFooterView;
                assert footView != null;
                ((SwipeTrigger) footView).onComplete();
            }
        }

        @Override
        public void onReset() {
            if (isLoadMoreEnabled() && STATUS.isStatusDefault(mStatus)) {
                View footView = mFooterView;
                assert footView != null;
                ((SwipeTrigger) footView).onReset();
                footView.setVisibility(GONE);
            }
        }
    };

    private class AutoScroller implements Runnable {

        private Scroller mScroller;

        private int mmLastY;

        private boolean mRunning = false;

        private boolean mAbort = false;

        public AutoScroller() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int currY = mScroller.getCurrY();
            int yDiff = currY - mmLastY;
            if (finish) {
                finish();
            } else {
                mmLastY = currY;
                SwipeToLoadLayout.this.autoScroll(yDiff);
                post(this);
            }
        }

        /**
         * remove the post callbacks and reset default values
         */
        private void finish() {
            mmLastY = 0;
            mRunning = false;
            removeCallbacks(this);
            // if abort by user, don't call
            if (!mAbort) {
                autoScrollFinished();
            }
        }

        /**
         * abort scroll if it is scrolling
         */
        public void abortIfRunning() {
            if (mRunning) {
                if (!mScroller.isFinished()) {
                    mAbort = true;
                    mScroller.forceFinished(true);
                }
                finish();
                mAbort = false;
            }
        }

        /**
         * The param yScrolled here isn't final pos of y.
         * It's just like the yScrolled param in the
         * {@link #updateScroll(float yScrolled)}
         */
        private void autoScroll(int yScrolled, int duration) {
            removeCallbacks(this);
            mmLastY = 0;
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, yScrolled, duration);
            post(this);
            mRunning = true;
        }
    }

    /**
     * Set the current status for better control
     */
    private void setStatus(int status) {
        mStatus = status;
        if (mDebug) {
            STATUS.printStatus(status);
        }
    }

    /**
     * an inner util class.
     * enum of status
     */
    private final static class STATUS {
        private static final int STATUS_REFRESH_RETURNING = -4;
        private static final int STATUS_REFRESHING = -3;
        private static final int STATUS_RELEASE_TO_REFRESH = -2;
        private static final int STATUS_SWIPING_TO_REFRESH = -1;
        private static final int STATUS_DEFAULT = 0;
        private static final int STATUS_SWIPING_TO_LOAD_MORE = 1;
        private static final int STATUS_RELEASE_TO_LOAD_MORE = 2;
        private static final int STATUS_LOADING_MORE = 3;
        private static final int STATUS_LOAD_MORE_RETURNING = 4;

        private static boolean isRefreshing(final int status) {
            return status == STATUS.STATUS_REFRESHING;
        }

        private static boolean isLoadingMore(final int status) {
            return status == STATUS.STATUS_LOADING_MORE;
        }

        private static boolean isReleaseToRefresh(final int status) {
            return status == STATUS.STATUS_RELEASE_TO_REFRESH;
        }

        private static boolean isReleaseToLoadMore(final int status) {
            return status == STATUS.STATUS_RELEASE_TO_LOAD_MORE;
        }

        private static boolean isSwipingToRefresh(final int status) {
            return status == STATUS.STATUS_SWIPING_TO_REFRESH;
        }

        private static boolean isSwipingToLoadMore(final int status) {
            return status == STATUS.STATUS_SWIPING_TO_LOAD_MORE;
        }

        private static boolean isRefreshStatus(final int status) {
            return status < STATUS.STATUS_DEFAULT;
        }

        public static boolean isLoadMoreStatus(final int status) {
            return status > STATUS.STATUS_DEFAULT;
        }

        private static boolean isStatusDefault(final int status) {
            return status == STATUS.STATUS_DEFAULT;
        }

        private static String getStatus(int status) {
            final String statusInfo;
            switch (status) {
                case STATUS_REFRESH_RETURNING:
                    statusInfo = "status_refresh_returning";
                    break;
                case STATUS_REFRESHING:
                    statusInfo = "status_refreshing";
                    break;
                case STATUS_RELEASE_TO_REFRESH:
                    statusInfo = "status_release_to_refresh";
                    break;
                case STATUS_SWIPING_TO_REFRESH:
                    statusInfo = "status_swiping_to_refresh";
                    break;
                case STATUS_DEFAULT:
                    statusInfo = "status_default";
                    break;
                case STATUS_SWIPING_TO_LOAD_MORE:
                    statusInfo = "status_swiping_to_load_more";
                    break;
                case STATUS_RELEASE_TO_LOAD_MORE:
                    statusInfo = "status_release_to_load_more";
                    break;
                case STATUS_LOADING_MORE:
                    statusInfo = "status_loading_more";
                    break;
                case STATUS_LOAD_MORE_RETURNING:
                    statusInfo = "status_load_more_returning";
                    break;
                default:
                    statusInfo = "status_illegal!";
                    break;
            }
            return statusInfo;
        }

        private static void printStatus(int status) {
            Log.i(TAG, "printStatus:" + getStatus(status));
        }
    }
}
