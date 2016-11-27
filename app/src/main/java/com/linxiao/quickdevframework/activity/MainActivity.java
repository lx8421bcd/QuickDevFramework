package com.linxiao.quickdevframework.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.support.AlertDialogWrapper;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.SampleApplication;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                AlertDialogWrapper.showTopActivityDialog(MainActivity.this, "message", "message");
                AlertDialogWrapper.showDialog("message");
            }
        });
    }



}
