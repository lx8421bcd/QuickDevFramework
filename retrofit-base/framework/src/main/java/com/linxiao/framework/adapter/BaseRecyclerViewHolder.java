package com.linxiao.framework.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 * Created by linxiao on 2017/1/9.
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }
    /**
     * 给ViewHolder设置数据，在这里实现
     * */
    public abstract<T> void setData(T data);
}
