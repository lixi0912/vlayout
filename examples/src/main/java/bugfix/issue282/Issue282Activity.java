package bugfix.issue282;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.example.DebugActivity;
import com.alibaba.android.vlayout.example.R;
import com.alibaba.android.vlayout.layout.FixLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.android.vlayout.layout.FixLayoutHelper.TOP_RIGHT;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/11
 */
public class Issue282Activity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue_282);


        RecyclerView recyclerView = findViewById(R.id.main_view);
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        recyclerView.setLayoutManager(virtualLayoutManager);

        final DynamicAdapter itemsAdapter = new DynamicAdapter();
        LinearLayoutHelper layoutHelper = new LinearLayoutHelper();
        layoutHelper.setBgColor(Color.WHITE);


        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(new WrapperAdapter<>(itemsAdapter, layoutHelper));
        recyclerView.setAdapter(delegateAdapter);


        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dataSize = itemsAdapter.getItemCount();
                itemsAdapter.getList().add(String.valueOf(dataSize + 1));
                itemsAdapter.notifyItemInserted(dataSize);
            }
        });

        findViewById(R.id.minus_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemsAdapter.getList().isEmpty()) {
                    itemsAdapter.getList().remove(0);
                    itemsAdapter.notifyItemRemoved(0);
                }
            }
        });
    }


}
