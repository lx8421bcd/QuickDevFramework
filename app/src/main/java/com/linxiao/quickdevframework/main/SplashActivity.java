package com.linxiao.quickdevframework.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.linxiao.framework.architecture.BaseSplashActivity;
import com.linxiao.quickdevframework.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseSplashActivity {

    private boolean dataReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        keepSplashScreenUntilInitFinished();
        execInit();
    }

    private void execInit() {
        new Handler().postDelayed(() -> {
            dataReady = true;
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }

    @Override
    protected boolean isInitFinished() {
        return dataReady;
    }
}
