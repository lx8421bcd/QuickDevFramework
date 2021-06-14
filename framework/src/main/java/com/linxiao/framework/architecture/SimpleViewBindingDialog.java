package com.linxiao.framework.architecture;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

public abstract class SimpleViewBindingDialog<B extends ViewBinding> extends Dialog {

    private B binding = null;

    @SuppressWarnings("unchecked")
    public SimpleViewBindingDialog(@NonNull Context context) {
        super(context);
        setViewBinding((Class<B>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    public SimpleViewBindingDialog(@NonNull  Context context, int themeResId) {
        super(context, themeResId);
        setViewBinding((Class<B>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    public SimpleViewBindingDialog(@NonNull  Context context, boolean cancelable, @Nullable  OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setViewBinding((Class<B>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    protected B getViewBinding() {
        return binding;
    }

    @SuppressWarnings("unchecked")
    protected void setViewBinding(Class<B> bindingClass) {
        try {
            binding = (B) bindingClass.getMethod("inflate", LayoutInflater.class)
                    .invoke(null, LayoutInflater.from(getContext()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        getWindow().getDecorView().setPadding(0,0,0,0);
    }
}
