package com.linxiao.quickdevframework.frameworkapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.linxiao.framework.support.ToastWrapper;
import com.linxiao.quickdevframework.R;

public class ToastApiActivity extends AppCompatActivity {

    int toastNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_api);
    }

    public void onShowToastClick(View v) {
        ToastWrapper.showToast(this, "toast " + toastNum++);
    }

}
