package com.linxiao.framework.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 基类
 * TODO：添加Header和Footer支持， Header、Footer控制
 * TODO：复杂数据基类，混合数据基类
 * Created by linxiao on 2017/1/4.
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<VH> {

    public interface OnItemClickListener {

        void onItemClick(BaseRecyclerViewAdapter adapter, View itemView, int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(BaseRecyclerViewAdapter adapter, View itemView, int position);
    }

    private static String TAG;

    private List<T> mDataSource;
    private Context mContext;

    public BaseRecyclerViewAdapter(Context context) {
        TAG = this.getClass().getSimpleName();
        mContext = context;
        mDataSource = new ArrayList<>();

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.setData(mDataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    protected View inflateItemView(@LayoutRes int layoutRes, @Nullable ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutRes, parent, false);
    }

    public void setDataSource(List<T> dataSource) {
        this.mDataSource.clear();
        this.mDataSource.addAll(dataSource);
        this.notifyDataSetChanged();
    }

    public void addToDataSource(T data) {
        this.mDataSource.add(data);
        this.notifyItemInserted(mDataSource.size());
    }

    public void addToDataSource(List<T> data) {
        this.mDataSource.addAll(data);
        this.notifyItemRangeInserted(mDataSource.size() - data.size(), data.size());
    }

    public void insertIntoDataSource(int position, T data) {
        if (position < 0 || position >= mDataSource.size() || data == null) {
            return;
        }
        this.mDataSource.add(position, data);
        this.notifyItemInserted(position);
    }

    public void insertIntoDataSource(int position, List<T> data) {
        if (position < 0 || position >= mDataSource.size() || data == null || data.size() == 0) {
            return;
        }
        this.mDataSource.addAll(position, data);
        this.notifyItemRangeInserted(position, data.size());
    }

    public void removeFromDataSource(int position) {
        if (position < 0 || position >= mDataSource.size()) {
            return;
        }
        this.mDataSource.remove(position);
        this.notifyItemRemoved(position);
    }

    public void removeFromDataSource(T data) {
        if (data == null) {
            return;
        }
        int position = mDataSource.indexOf(data);
        mDataSource.remove(data);
        this.notifyItemRemoved(position);
    }

    public void removeFromDataSource(List<T> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        for (T t : data) {
            int position = mDataSource.indexOf(t);
            mDataSource.remove(t);
            this.notifyItemRemoved(position);
        }
    }

    public void removeAll() {
        int count = mDataSource.size();
        this.mDataSource.clear();
        this.notifyItemRangeRemoved(0, count);
    }

    public List<T> getDataSource() {
        return mDataSource;
    }

    protected Context getContext() {
        return mContext;
    }

}
