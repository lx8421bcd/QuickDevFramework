package com.linxiao.framework.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;

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

    @SuppressWarnings("unchecked")
    public static <B extends ViewBinding> ViewBindingRecyclerHolder<B> create(Class<B> bindingClass, ViewGroup parent) {
        B binding = null;
        try {
            binding = (B) bindingClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class)
                    .invoke(null, LayoutInflater.from(parent.getContext()), parent, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        ViewBindingRecyclerHolder<B> holder = new ViewBindingRecyclerHolder<>(binding.getRoot());
        holder.binding = binding;
        return holder;
    }

    private ViewBindingRecyclerHolder(View itemView) {
        super(itemView);
    }

    public B getViewBinding() {
        return binding;
    }

}
