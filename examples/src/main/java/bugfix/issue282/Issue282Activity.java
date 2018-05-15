package bugfix.issue282;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.ItemAnimatorWatcher;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.example.DebugActivity;
import com.alibaba.android.vlayout.example.R;
import com.alibaba.android.vlayout.layout.FixLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alibaba.android.vlayout.layout.FixLayoutHelper.TOP_RIGHT;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/11
 */
public class Issue282Activity extends AppCompatActivity {

    private AtomicInteger idCreator = new AtomicInteger();

    private static final class FixItemAnimator extends DefaultItemAnimator implements ItemAnimatorWatcher {

        private StartedListener listener;

        @Override
        public void onRemoveStarting(RecyclerView.ViewHolder item) {
            super.onRemoveStarting(item);
            if (null!=listener){
                listener.onAnimationStarted(item);
            }
        }

        @Override
        public void setItemAnimatorStartedListener(StartedListener listener) {
            this.listener =listener;
        }

        @Override
        public StartedListener getItemAnimatorStartedListener() {
            return listener;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue_282);


        RecyclerView recyclerView = findViewById(R.id.main_view);
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        recyclerView.setLayoutManager(virtualLayoutManager);
        recyclerView.setItemAnimator(null);

        final int spanCount = 3;
        final DynamicAdapter itemsAdapter = new DynamicAdapter(idCreator);
        GridLayoutHelper layoutHelper = new GridLayoutHelper(spanCount);
        layoutHelper.setBgColor(Color.WHITE);


        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(new WrapperAdapter<>(itemsAdapter, layoutHelper));
        recyclerView.setAdapter(delegateAdapter);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dataSize = itemsAdapter.getItemCount();
                itemsAdapter.getList().add(String.valueOf(idCreator.incrementAndGet()));
                itemsAdapter.notifyItemInserted(dataSize);
            }
        });

//        final Random random = new Random();
        findViewById(R.id.minus_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> strings = itemsAdapter.getList();
                int itemCount = 0;

//                final int removedSpanCount = random.nextInt(spanCount) + 1;
                final int removedSpanCount = spanCount;
                for (int i = 0; i < removedSpanCount; i++) {
                    if (!strings.isEmpty()) {
                        strings.remove(0);
                        itemCount++;
                    }
                }

                itemsAdapter.notifyItemRangeRemoved(0, itemCount);
            }
        });
    }


}
