package com.alibaba.android.vlayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2017/6/26
 */
public class WrapperAdapter<VH extends RecyclerView.ViewHolder> extends DelegateAdapter.Adapter<VH> {

    private final RecyclerView.Adapter<VH> delegate;
    private final LayoutHelper helper;

    public WrapperAdapter(@NonNull RecyclerView.Adapter<VH> delegate, @NonNull LayoutHelper helper) {
        this.delegate = delegate;
        this.helper = helper;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return helper;
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        return delegate.onFailedToRecycleView(holder);
    }


    @Override
    public int getItemViewType(int position) {
        return delegate.getItemViewType(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        delegate.setHasStableIds(hasStableIds);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        delegate.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        delegate.unregisterAdapterDataObserver(observer);
    }

    @Override
    public long getItemId(int position) {
        return delegate.getItemId(position);
    }

    @Override
    public void onViewRecycled(VH holder) {
        delegate.onViewRecycled(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        delegate.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        delegate.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        delegate.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        delegate.onViewDetachedFromWindow(holder);
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return delegate.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        delegate.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        // no-op
    }

    @Override
    public int getItemCount() {
        return delegate.getItemCount();
    }
}
