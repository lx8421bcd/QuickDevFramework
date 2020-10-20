package com.linxiao.quickdevframework.main;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.linxiao.framework.architecture.BaseActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

/**
 * <p>
 * class usage summary
 * </p>
 *
 * @author linxiao
 * @since 2020-10-20
 */
public abstract class SimpleViewBindingActivity<B extends ViewBinding> extends BaseActivity {

    private B binding = null;

    protected B getViewBinding() {
        return binding;
    }

    @SuppressWarnings("unchecked")
    protected void setViewBinding(Class<B> bindingClass) {
        try {
            binding = (B) bindingClass.getMethod("inflate", LayoutInflater.class)
                    .invoke(null, getLayoutInflater());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        setContentView(binding.getRoot());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewBinding((Class<B>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
