package com.linxiao.framework.list;

import android.content.Context;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * <p>
 * SingleItemAdapter with {@link ViewBindingRecyclerHolder}
 * use this type to avoid to write the ViewHolder creation codes.
 * </p>
 *
 * @author linxiao
 * @since 2020-10-20
 */
public abstract class ViewBindingSingleItemAdapter<T, B extends ViewBinding> extends SingleItemAdapter<T, ViewBindingRecyclerHolder<B>> {


    public ViewBindingSingleItemAdapter(Context context) {
        super(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ViewBindingRecyclerHolder<B> onCreateDataViewHolder(ViewGroup parent, int viewType) {
        return ViewBindingRecyclerHolder.create((Class<B>) ((ParameterizedType) Objects.requireNonNull(getClass()
                .getGenericSuperclass())).getActualTypeArguments()[1], parent);
    }
}
