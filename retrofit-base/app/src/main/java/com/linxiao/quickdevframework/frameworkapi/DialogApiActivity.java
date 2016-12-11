package com.linxiao.quickdevframework.frameworkapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.quickdevframework.R;

public class DialogApiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_api);
    }

    public void onSimpleDialogClick(View v) {
        AlertDialogWrapper.showAlertDialog("simple dialog messages");
    }

    public void onShowTopDialogClick(View v) {
        Intent backServiceIntent = new Intent(this, BackgroundService.class);
        startService(backServiceIntent);
    }

    public void onShowBottomDialogClick(View v) {
        SampleBottomDialogFragment dialogFragment = new SampleBottomDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "SampleDialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent backServiceIntent = new Intent(this, BackgroundService.class);
//        stopService(backServiceIntent);
    }
}
