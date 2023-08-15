package com.linxiao.quickdevframework.main;

import androidx.annotation.NonNull;

import com.linxiao.framework.list.ViewBindingRecyclerHolder;
import com.linxiao.framework.list.ViewBindingSingleItemAdapter;
import com.linxiao.quickdevframework.databinding.ItemApiSampleBinding;

/**
 *
 * Created by linxiao on 2016/11/30.
 */
public class ApiSampleListAdapter extends ViewBindingSingleItemAdapter<ApiSampleObject, ItemApiSampleBinding> {


    @Override
    protected void onBindViewHolder(@NonNull ViewBindingRecyclerHolder<ItemApiSampleBinding> holder, int position, ApiSampleObject item) {
        super.onBindViewHolder(holder, position, item);
        holder.getViewBinding().tvApiSample.setText(item.getApiName());
    }

}
