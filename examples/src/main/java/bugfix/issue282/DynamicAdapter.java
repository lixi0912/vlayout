package bugfix.issue282;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.example.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/11
 */
public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleText;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mTitleText = itemView.findViewById(R.id.title);
        }
    }

    private LayoutInflater mInflater;

    private List<String> mList;

    DynamicAdapter(AtomicInteger idCreator) {
        final List<String> data = new ArrayList<>();
        final int size = 10;
        for (int i = 0; i < size; i++) {
            data.add(String.valueOf(idCreator.incrementAndGet()));
        }
        this.mList = data;
    }

    public DynamicAdapter(@NonNull List<String> list) {
        this.mList = list;
    }

    @NonNull
    public List<String> getList() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    private void assertInflaterCreated(Context context) {
        if (null == mInflater) {
            mInflater = LayoutInflater.from(context);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assertInflaterCreated(parent.getContext());
        return new ViewHolder(mInflater.inflate(R.layout.item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = mList.get(position);
        holder.mTitleText.setText(title);
    }


}
