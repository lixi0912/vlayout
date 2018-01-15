package issue206;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.example.R;
import com.alibaba.android.vlayout.example.VLayoutActivity;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/1/15
 */

public class Issue206Activity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_206);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_view);
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        final DelegateAdapter delegateAdapter = new DelegateAdapter(layoutManager);
        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();


        LinearLayoutHelper firstLayoutHelper = new LinearLayoutHelper();
        final VLayoutActivity.SubAdapter firstAdapter = new VLayoutActivity.SubAdapter(this, firstLayoutHelper, 0);
        adapters.add(firstAdapter);


        LinearLayoutHelper secLayoutHelper = new LinearLayoutHelper();
        VLayoutActivity.SubAdapter secAdapter = new VLayoutActivity.SubAdapter(this, secLayoutHelper, 10);
        adapters.add(secAdapter);

        delegateAdapter.setAdapters(adapters);
        recyclerView.setAdapter(delegateAdapter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                firstAdapter.addCount(10);
                firstAdapter.notifyItemRangeInserted(0,
                        firstAdapter.getItemCount());
                String msg = String.format(Locale.getDefault(),
                        "notifyItemRangeInserted(0,%d)",
                        firstAdapter.getItemCount());
                Toast.makeText(Issue206Activity.this, msg, Toast.LENGTH_SHORT)
                        .show();
            }
        }, 1500);

    }


}
