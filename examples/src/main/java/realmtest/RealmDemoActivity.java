package realmtest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.example.R;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import realmtest.bean.Book;
import realmtest.bean.IPrinter;
import realmtest.bean.Toy;

public class RealmDemoActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "RealmDemo";

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_demo);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);

        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RealmResults<Book> books = realm.where(Book.class).findAllSorted("id");
        Adapter<Book> bookAdapter = new Adapter<>(books, true, new StaggeredGridLayoutHelper(), realm);


        RealmResults<Toy> toys = realm.where(Toy.class).findAllSorted("id");
        Adapter<Toy> toyAdapter = new Adapter<>(toys, true, new LinearLayoutHelper(), realm);


        List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
        adapters.add(new StickyTitleAdapter("Book"));
        adapters.add(bookAdapter);
        adapters.add(new StickyTitleAdapter("Toy"));
        adapters.add(toyAdapter);

        final DelegateAdapter delegateAdapter = new DelegateAdapter(layoutManager, true);
        delegateAdapter.setAdapters(adapters);

        recyclerView.setAdapter(delegateAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {

                        int originPos = viewHolder.getAdapterPosition();
                        int targetPos = target.getAdapterPosition();




                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int itemPos = delegateAdapter.itemIndexOf(viewHolder);
                        Log.d(TAG, "itemPos:" + itemPos);


                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnChildAttachStateChangeListener(itemTouchHelper);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_book_object:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Book.add(realm);
                    }
                });
                break;
            case R.id.add_toy_object:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Toy.add(realm);
                    }
                });
                break;
        }
    }


    static class Adapter<T extends RealmModel & IPrinter> extends RealmRecyclerViewAdapter<T, ViewHolder> {

        private final LayoutHelper layoutHelper;
        private final Realm realm;

        public Adapter(@Nullable OrderedRealmCollection<T> data,
                       boolean autoUpdate,
                       LayoutHelper layoutHelper, Realm realm) {
            super(data, autoUpdate);
            this.layoutHelper = layoutHelper;
            this.realm = realm;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return layoutHelper;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_realm, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            T printer = getItem(position);
            holder.adapter = this;
            holder.data = printer;
            TextView textView = (TextView) holder.itemView;
            textView.setText(null != printer ? printer.print(position) : String.format(Locale.getDefault(), "%d was removed", position));
        }

        void deleteItem(final T data) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmObject.deleteFromRealm(data);
                }
            });
        }
    }


    public static class ViewHolder<T extends RealmModel & IPrinter> extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static volatile int existing = 0;
        public static int createdTimes = 0;
        public T data;
        public Adapter<T> adapter;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            createdTimes++;
            existing++;
            Log.e(TAG, String.format(Locale.getDefault(), "createdTimes:%1$d,existing:%2$d", createdTimes, existing));
        }

        @Override
        protected void finalize() throws Throwable {
            existing--;
            super.finalize();
        }

        @Override
        public void onClick(View v) {
            if (null != data && null != adapter && RealmObject.isValid(data)) {
                Log.d(TAG, data.print(getAdapterPosition()));
                adapter.deleteItem(data);
            } else {
                Log.d(TAG, "data was null or not valid");
            }
        }
    }

    public static class StickyTitleAdapter extends DelegateAdapter.Adapter<TitleViewHolder> {

        private final StickyLayoutHelper layoutHelper;
        private final String title;

        public StickyTitleAdapter(String title) {
            this.title = title;
            this.layoutHelper = new StickyLayoutHelper();
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return layoutHelper;
        }

        @Override
        public TitleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title, parent, false));
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public void onBindViewHolder(TitleViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setId(holder.getAdapterPosition());
            textView.setText(title);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {

        public TitleViewHolder(View itemView) {
            super(itemView);
        }
    }

}
