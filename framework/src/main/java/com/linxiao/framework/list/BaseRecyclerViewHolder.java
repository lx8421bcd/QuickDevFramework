package com.linxiao.framework.list;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
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
    protected  <T extends View> T findView(@IdRes int resId) {
        return (T) itemView.findViewById(resId);
    }

}