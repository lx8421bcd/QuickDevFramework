package com.linxiao.quickdevframework.sample.adapter;

import android.os.Handler;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.linxiao.framework.architecture.BaseActivity;
import com.linxiao.framework.common.ScreenUtil;
import com.linxiao.framework.list.EquidistantDecoration;
import com.linxiao.quickdevframework.R;

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
        rcvEmptySimple.setLayoutManager(new GridLayoutManager(this, 3));
        rcvEmptySimple.addItemDecoration(new EquidistantDecoration(3, ScreenUtil.dp2px(12)));

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
        refreshData();
    }

    @OnClick(R.id.btnRefresh)
    public void onBtnRefreshClick(View v) {
        showEmpty = true;
        showError = false;
        showData = false;
        mAdapter.removeAll();
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
