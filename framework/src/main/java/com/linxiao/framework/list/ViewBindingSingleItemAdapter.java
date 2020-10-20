package com.linxiao.framework.list;

import android.content.Context;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import java.lang.reflect.ParameterizedType;

/**
 * <p>
 * class usage summary
 * </p>
 *
 * @author linxiao
 * @since 2020-10-20
 */
public abstract class ViewBindingSingleItemAdapter<T, B extends ViewBinding> extends SingleItemAdapter<T, ViewBindingRecyclerHolder<B>> {


    public ViewBindingSingleItemAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewBindingRecyclerHolder<B> onCreateDataViewHolder(ViewGroup parent, int viewType) {
        return ViewBindingRecyclerHolder.create((Class<B>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1], parent);
    }
}
