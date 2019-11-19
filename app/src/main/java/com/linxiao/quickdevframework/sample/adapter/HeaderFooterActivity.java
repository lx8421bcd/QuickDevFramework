package com.linxiao.quickdevframework.sample.adapter;

import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.linxiao.framework.architecture.BaseActivity;
import com.linxiao.quickdevframework.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderFooterActivity extends BaseActivity {

    @BindView(R.id.rcvHeaderFooter)
    RecyclerView rcvHeaderFooter;

    SampleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_footer);
        ButterKnife.bind(this);

        mAdapter = new SampleAdapter(this);
        rcvHeaderFooter.setAdapter(mAdapter);
        rcvHeaderFooter.setItemAnimator(new DefaultItemAnimator());
        rcvHeaderFooter.setLayoutManager(new LinearLayoutManager(this));

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
