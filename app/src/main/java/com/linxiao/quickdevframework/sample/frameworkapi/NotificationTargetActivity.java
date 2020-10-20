package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import android.view.View;

import com.linxiao.quickdevframework.databinding.ActivityNotificationTargetBinding;
import com.linxiao.quickdevframework.main.SimpleViewBindingActivity;

public class NotificationTargetActivity extends SimpleViewBindingActivity<ActivityNotificationTargetBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewBinding().tvTargetDesc.setOnClickListener(this::onTextViewClick);
    }

    public void onTextViewClick(View v) {
        finish();
    }
}
