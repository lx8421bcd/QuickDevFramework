package com.linxiao.quickdevframework.sample.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.linxiao.framework.list.SingleItemRecyclerAdapter;
import com.linxiao.framework.list.BaseRecyclerViewHolder;
import com.linxiao.quickdevframework.R;

/**
 *
 * Created by linxiao on 2017/1/16.
 */
public class SampleAdapter extends SingleItemRecyclerAdapter<String, BaseRecyclerViewHolder> {


    public SampleAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setData(BaseRecyclerViewHolder holder, int position, String data) {
        //NO IMPLEMENTATION
    }

    @Override
    protected BaseRecyclerViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(inflateItemView(R.layout.item_list_sample, parent));
    }
}
