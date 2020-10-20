package com.linxiao.quickdevframework.sample.adapter;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.ActivityHeaderFooterBinding;
import com.linxiao.quickdevframework.main.SimpleViewBindingActivity;

public class HeaderFooterActivity extends SimpleViewBindingActivity<ActivityHeaderFooterBinding> {

    SampleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SampleAdapter(this);
        getViewBinding().rcvHeaderFooter.setAdapter(mAdapter);
        getViewBinding().rcvHeaderFooter.setItemAnimator(new DefaultItemAnimator());
        getViewBinding().rcvHeaderFooter.setLayoutManager(new LinearLayoutManager(this));

        View sampleHeader = getLayoutInflater().inflate(R.layout.header_sample, null);
        sampleHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHeaderView();
            }
        });
        mAdapter.addHeaderView(sampleHeader);

        View sampleFooter = getLayoutInflater().inflate(R.layout.footer_sample, null);
        sampleFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFooterView();
            }
        });
        mAdapter.addFooterView(sampleFooter);

        initData();
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mAdapter.addToDataSource("");
        }
    }

    private void addHeaderView() {
        View addedHeader = getLayoutInflater().inflate(R.layout.header_added, null);
        addedHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.removeHeaderView(view);
            }
        });
        mAdapter.addHeaderView(addedHeader);
    }

    private void addFooterView() {
        View addedFooter = getLayoutInflater().inflate(R.layout.footer_added, null);
        addedFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.removeFooterView(view);
            }
        });
        mAdapter.addFooterView(addedFooter);
    }
}
