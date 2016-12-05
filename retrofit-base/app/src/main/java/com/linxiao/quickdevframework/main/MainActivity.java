package com.linxiao.quickdevframework.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.support.AlertDialogWrapper;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.frameworkapi.DialogApiActivity;
import com.linxiao.quickdevframework.frameworkapi.NotificationApiActivity;
import com.linxiao.quickdevframework.frameworkapi.PermissionApiActivity;
import com.linxiao.quickdevframework.frameworkapi.ToastApiActivity;
import com.linxiao.quickdevframework.netapi.NetTestActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rcvApiSampleList)
    RecyclerView rcvApiSampleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initApiSampleList();
    }

    private void initApiSampleList() {
        List<ApiSampleObject> apiSampleList = new ArrayList<>();
        apiSampleList.add(new ApiSampleObject("Dialog API", DialogApiActivity.class));
        apiSampleList.add(new ApiSampleObject("Notification API", NotificationApiActivity.class));
        apiSampleList.add(new ApiSampleObject("Toast API", ToastApiActivity.class));
        apiSampleList.add(new ApiSampleObject("Permission API", PermissionApiActivity.class));
        apiSampleList.add(new ApiSampleObject("Network API", NetTestActivity.class));

        ApiSampleListAdapter adapter = new ApiSampleListAdapter(this);
        adapter.setDataSource(apiSampleList);

        rcvApiSampleList.setLayoutManager(new LinearLayoutManager(this));
        rcvApiSampleList.setItemAnimator(new DefaultItemAnimator());
        rcvApiSampleList.setAdapter(adapter);

    }


}
