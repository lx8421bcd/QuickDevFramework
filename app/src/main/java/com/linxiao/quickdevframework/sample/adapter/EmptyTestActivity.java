package com.linxiao.quickdevframework.sample.adapter;

import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.linxiao.framework.common.ScreenUtil;
import com.linxiao.framework.list.EquidistantDecoration;
import com.linxiao.quickdevframework.databinding.ActivityEmptyTestBinding;
import com.linxiao.framework.architecture.SimpleViewBindingActivity;

import java.util.Arrays;

public class EmptyTestActivity extends SimpleViewBindingActivity<ActivityEmptyTestBinding> {

    SampleAdapter mAdapter;

    private boolean showEmpty = true;
    private boolean showData = false;
    private boolean showError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SampleAdapter(this);
        getViewBinding().rcvEmptySimple.setAdapter(mAdapter);
        getViewBinding().rcvEmptySimple.setItemAnimator(new DefaultItemAnimator());
        getViewBinding().rcvEmptySimple.setLayoutManager(new GridLayoutManager(this, 3));
        getViewBinding().rcvEmptySimple.addItemDecoration(new EquidistantDecoration(3, ScreenUtil.dp2px(12)));

//        View emptyView = getLayoutInflater().inflate(R.layout.empty_view, null);
//        mAdapter.setEmptyView(emptyView);
//
//        View loadingView = getLayoutInflater().inflate(R.layout.loading_view, null);
//        mAdapter.setLoadingView(loadingView);
//
//        View errorView = getLayoutInflater().inflate(R.layout.error_view, null);
//        mAdapter.setErrorView(errorView);
//        errorView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                refreshData();
//            }
//        });
//        emptyView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                refreshData();
//            }
//        });
        getViewBinding().btnRefresh.setOnClickListener(v -> {
            showEmpty = true;
            showError = false;
            showData = false;
            mAdapter.removeAll();
            refreshData();
        });
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
                    mAdapter.addToDataSource(Arrays.asList("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"));
                }
            }
        }, 1000);
        mAdapter.addToDataSource(Arrays.asList("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"));
    }
}
