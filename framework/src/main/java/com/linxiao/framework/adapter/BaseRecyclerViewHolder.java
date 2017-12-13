package com.linxiao.framework.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 *
 * Created by linxiao on 2017/1/9.
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    private int dataPosition;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public void setDataPosition(int position) {
        dataPosition = position;
    }

    protected int getDataPosition() {
        return dataPosition;
    }

    @SuppressWarnings("unchecked")
    protected  <T extends View> T findView(View v, @IdRes int resId) {
        return (T) v.findViewById(resId);
    }

    @SuppressWarnings("unchecked")
    protected  <T extends View> T findView(@IdRes int resId) {
        if (itemView == null) {
            return null;
        }
        return (T) itemView.findViewById(resId);
    }

}