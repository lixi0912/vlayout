package issue223;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.example.R;
import com.alibaba.android.vlayout.example.VLayoutActivity;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.ArrayList;
import java.util.List;

import issue223.swipetoloadlayout.OnLoadMoreListener;
import issue223.swipetoloadlayout.SwipeToLoadLayout;

/**
 * * https://github.com/alibaba/vlayout/issues/223#issuecomment-356802156
 *
 * @author lixi
 * @description <>
 * @date 2018/1/12
 */
public class Issue223Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_issue_223);


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);

        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        final DelegateAdapter delegateAdapter = new DelegateAdapter(layoutManager);

        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();


        int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
        LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper();
        linearLayoutHelper.setDividerHeight(dividerHeight);


        adapters.add(new VLayoutActivity.SubAdapter(this, linearLayoutHelper, 20));

        delegateAdapter.setAdapters(adapters);
        recyclerView.setAdapter(delegateAdapter);


        final Handler handler = new Handler();
        final SwipeToLoadLayout loadLayout = (SwipeToLoadLayout) findViewById(R.id.swipe_load_frame);
        loadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });

    }
}
