package com.linxiao.framework.architecture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
public abstract class SimpleViewBindingFragment<B extends ViewBinding> extends BaseFragment {

    private B binding = null;

    protected B getViewBinding() {
        return binding;
    }

    @SuppressWarnings("unchecked")
    protected void setViewBinding(Class<B> bindingClass) {
        try {
            binding = (B) bindingClass.getMethod("inflate", LayoutInflater.class)
                    .invoke(null, getLayoutInflater());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setViewBinding((Class<B>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return binding.getRoot();
    }

}
