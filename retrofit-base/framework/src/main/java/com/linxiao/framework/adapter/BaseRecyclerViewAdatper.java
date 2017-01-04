package com.linxiao.framework.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 基类
 * Created by linxiao on 2017/1/4.
 */
public abstract class BaseRecyclerViewAdatper<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private static String TAG;

    private List<T> mDataSource;

    public BaseRecyclerViewAdatper() {
        TAG = this.getClass().getSimpleName();
        mDataSource = new ArrayList<>();
    }

    public List<T> getDataSource() {
        return mDataSource;
    }

    public void setDataSource(List<T> dataSource) {
        this.mDataSource = dataSource;
    }


}
