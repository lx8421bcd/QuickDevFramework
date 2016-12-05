package com.linxiao.quickdevframework.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.linxiao.framework.activity.BaseSplashActivity;
import com.linxiao.quickdevframework.R;

public class SplashActivity extends BaseSplashActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                handleNotification();
                finish();
            }
        }, 1000);
    }


}
