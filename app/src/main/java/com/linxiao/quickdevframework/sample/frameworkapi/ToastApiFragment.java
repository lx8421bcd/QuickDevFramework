package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.common.ToastAlert;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.FragmentToastApiBinding;
import com.linxiao.framework.architecture.SimpleViewBindingFragment;

public class ToastApiFragment extends SimpleViewBindingFragment<FragmentToastApiBinding> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().btnTextShow.setOnClickListener(this::onBtnTextShowClick);
        getViewBinding().btnTextIconShow.setOnClickListener(this::onBtnTextIconShowClick);
        getViewBinding().btnTextEnqueue.setOnClickListener(this::onBtnTextEnqueueClick);
        getViewBinding().btnTextIconEnqueue.setOnClickListener(this::onBtnTextIconEnqueueClick);
        getViewBinding().btnPowerful.setOnClickListener(this::onBtnPowerfulClick);
    }

    public void onBtnTextShowClick(View v) {
        ToastAlert.show("show a text toast");
    }

    public void onBtnTextIconShowClick(View v) {
        ToastAlert.show("show a text toast with a icon", R.drawable.leak_canary_icon);
    }

    public void onBtnTextEnqueueClick(View v) {
        ToastAlert.show("enqueue a text toast");
    }

    public void onBtnTextIconEnqueueClick(View v) {
        ToastAlert.enqueue("enqueue a text toast with a icon", R.drawable.leak_canary_icon);
    }

    public void onBtnPowerfulClick(View v) {
        ToastAlert.create("powerful")
                .iconResId(R.drawable.leak_canary_icon)
                .duration(100)
                .gravity(Gravity.TOP, 200)
                .show();
    }
}
