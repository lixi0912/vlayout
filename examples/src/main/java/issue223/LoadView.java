package issue223;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.alibaba.android.vlayout.example.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import issue223.swipetoloadlayout.SwipeLoadMoreTrigger;
import issue223.swipetoloadlayout.SwipeRefreshTrigger;
import issue223.swipetoloadlayout.SwipeToLoadLayout;

/**
 * @author lixi
 * @description <>
 * @date 2017/9/7
 */
public class LoadView extends ConstraintLayout implements SwipeRefreshTrigger, SwipeLoadMoreTrigger {




    private TextView mTitleTextView;


    public LoadView(Context context) {
        this(context, null);
    }

    public LoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_load, this);
        this.mTitleTextView = findViewById(R.id.mark_text);
        this.preText = "";


    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    public void onPrepare(SwipeToLoadLayout frame, int state) {
        setVisibility(mTitleTextView, VISIBLE);
    }

    String preText;

    private final static String[] STATE_MSG = {"", "释放并加载", "上拉加载更多", "释放并刷新", "下拉刷新"};


    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        String preText = this.preText;
        final int index;
        if (!isComplete) {
            if (yScrolled < 0) {
                if (yScrolled <= -getHeight()) {
                    index = 1;
                } else {
                    index = 2;
                }
            } else if (yScrolled >= getHeight()) {
                index = 3;
            } else {
                index = 4;
            }
        } else {
            index = 0;
        }

        String newText = STATE_MSG[index];

        if (null == preText || !preText.equals(newText)) {
            mTitleTextView.setText(newText);
            this.preText = newText;
        }
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {


    }

    @Override
    public void onReset() {
        resetView();
    }

    private void resetView() {
        preText = "";
        mTitleTextView.setText(null);
    }



    @Override
    public void onLoadMore() {
        mTitleTextView.setText("刷新中");
    }

    @Override
    public void onRefresh() {
        mTitleTextView.setText("刷新中");
    }

    private void setVisibility(View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    @Override
    public boolean canLoadMore() {
        return true;
    }




}
