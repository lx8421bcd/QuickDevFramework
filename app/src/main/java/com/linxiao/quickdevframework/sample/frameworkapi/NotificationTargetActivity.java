package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import android.view.View;

import com.linxiao.framework.architecture.BaseActivity;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationTargetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_target);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tvTargetDesc)
    public void onTextViewClick(View v) {
        finish();
    }
}
