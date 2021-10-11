package com.linxiao.framework.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * <p>
 * RecyclerViewHolder built with ViewBinding
 * the constructor method is private and use {@link #create(Class, ViewGroup)} method
 * to create ViewHolder.
 * use holder.getViewBinding.someView to get reference of View
 * </p>
 *
 * @author linxiao
 * @since 2020-10-20
 */
public class ViewBindingRecyclerHolder<B extends ViewBinding> extends BaseRecyclerViewHolder {

    private B binding = null;

    public static <B extends ViewBinding> ViewBindingRecyclerHolder<B> create(Class<B> bindingClass, ViewGroup parent) {
        return new ViewBindingRecyclerHolder<>(bindingClass, parent);
    }

    protected ViewBindingRecyclerHolder(Class<B> bindingClass, ViewGroup parent) {
        this(inflateItemView(bindingClass, parent));
        initViewBinding(bindingClass);
    }

    private ViewBindingRecyclerHolder(View itemView) {
        super(itemView);
    }

    public B getViewBinding() {
        return binding;
    }

    @SuppressWarnings("unchecked")
    private void initViewBinding(Class<B> bindingClass) {
        try {
            binding = (B) bindingClass.getMethod("bind", View.class).invoke(null, itemView);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static <B extends ViewBinding> View inflateItemView(Class<B> bindingClass, ViewGroup parent) {
        B binding = null;
        try {
            binding = (B) bindingClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class)
                    .invoke(null, LayoutInflater.from(parent.getContext()), parent, false);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(binding).getRoot();
    }
}
