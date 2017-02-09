package com.linxiao.quickdevframework.adaptertest;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.adaptertest.adapter.SampleAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmptyTestActivity extends BaseActivity {

    @BindView(R.id.rcvEmptySimple)
    RecyclerView rcvEmptySimple;

    SampleAdapter mAdapter;

    private boolean showEmpty = true;
    private boolean showData = false;
    private boolean showError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_test);
        ButterKnife.bind(this);
        mAdapter = new SampleAdapter(this);
        rcvEmptySimple.setAdapter(mAdapter);
        rcvEmptySimple.setItemAnimator(new DefaultItemAnimator());
        rcvEmptySimple.setLayoutManager(new LinearLayoutManager(this));

        View emptyView = getLayoutInflater().inflate(R.layout.empty_view, null);
        mAdapter.setEmptyView(emptyView);

        View loadingView = getLayoutInflater().inflate(R.layout.loading_view, null);
        mAdapter.setLoadingView(loadingView);

        View errorView = getLayoutInflater().inflate(R.layout.error_view, null);
        mAdapter.setErrorView(errorView);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
        refreshData();
    }

    @OnClick(R.id.btnRefresh)
    public void onBtnRefreshClick(View v) {
        showEmpty = true;
        showError = false;
        showData = false;
        refreshData();
    }

    private void refreshData() {
        mAdapter.showLoadingView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (showEmpty) {
                    showError = true;
                    showEmpty = false;
                    showData = false;
                    mAdapter.showEmptyView();
                }
                else if (showError) {
                    showData = true;
                    showEmpty = false;
                    showError = false;
                    mAdapter.showErrorView();
                }
                else if (showData) {
                    showEmpty = true;
                    showData = false;
                    showError = false;
                    mAdapter.addToDataSource(Arrays.asList("1","1","1","1","1","1","1","1","1","1","1"));
                }
            }
        }, 1000);
    }
}
