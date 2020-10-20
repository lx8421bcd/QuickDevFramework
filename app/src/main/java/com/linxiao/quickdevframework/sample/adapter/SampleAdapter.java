package com.linxiao.quickdevframework.sample.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.linxiao.framework.list.SingleItemAdapter;
import com.linxiao.framework.list.BaseRecyclerViewHolder;
import com.linxiao.framework.list.ViewBindingRecyclerHolder;
import com.linxiao.framework.list.ViewBindingSingleItemAdapter;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.ItemListSampleBinding;

/**
 *
 * Created by linxiao on 2017/1/16.
 */
public class SampleAdapter extends ViewBindingSingleItemAdapter<String, ItemListSampleBinding> {


    public SampleAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setData(ViewBindingRecyclerHolder<ItemListSampleBinding> holder, int position, String data) {
        //DO NOTHING
    }


}
