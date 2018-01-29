package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.toast.ToastAlert;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ToastApiFragment extends BaseFragment {

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_toast_api, container);
        ButterKnife.bind(this, getContentView());
    }

    @OnClick(R.id.btnTextShow)
    public void onBtnTextShowClick(View v) {
        ToastAlert.show("show a text toast");
    }

    @OnClick(R.id.btnTextIconShow)
    public void onBtnTextIconShowClick(View v) {
        ToastAlert.show("show a text toast with a icon", R.drawable.leak_canary_icon);
    }

    @OnClick(R.id.btnTextEnqueue)
    public void onBtnTextEnqueueClick(View v) {
        ToastAlert.show("enqueue a text toast");
    }

    @OnClick(R.id.btnTextIconEnqueue)
    public void onBtnTextIconEnqueueClick(View v) {
        ToastAlert.enqueue("enqueue a text toast with a icon", R.drawable.leak_canary_icon);
    }

    @OnClick(R.id.btnPowerful)
    public void onBtnPowerfulClick(View v) {
        ToastAlert.create("powerful")
                .iconResId(R.drawable.leak_canary_icon)
                .duration(100)
                .gravity(Gravity.TOP, 200)
                .show();
    }
}
