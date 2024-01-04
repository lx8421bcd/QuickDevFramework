package com.linxiao.quickdevframework.sample.adapter;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.QuickAdapterHelper;
import com.linxiao.framework.architecture.SimpleViewBindingActivity;
import com.linxiao.framework.list.HeaderFooterAdapter;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.ActivityHeaderFooterBinding;

import java.util.Arrays;

import kotlin.Unit;

public class HeaderFooterActivity extends SimpleViewBindingActivity<ActivityHeaderFooterBinding> {

    private SampleAdapter mAdapter;
    private QuickAdapterHelper quickAdapterHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SampleAdapter();
        mAdapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            addHeaderView();
            addFooterView();
        });
        quickAdapterHelper = new QuickAdapterHelper.Builder(mAdapter)
                .build();
        getViewBinding().rcvHeaderFooter.setLayoutManager(new LinearLayoutManager(this));
        getViewBinding().rcvHeaderFooter.setItemAnimator(new DefaultItemAnimator());
        getViewBinding().rcvHeaderFooter.setAdapter(quickAdapterHelper.getAdapter());
        HeaderFooterAdapter headerAdapter = new HeaderFooterAdapter(R.layout.header_sample);
        headerAdapter.setOnInitView(quickViewHolder -> {
            quickViewHolder.itemView.setOnClickListener(v -> {
                addHeaderView();
            });
            return Unit.INSTANCE;
        });
        quickAdapterHelper.addBeforeAdapter(headerAdapter);

        HeaderFooterAdapter footerAdapter = new HeaderFooterAdapter(R.layout.footer_sample);
        footerAdapter.setOnInitView(quickViewHolder -> {
            quickViewHolder.itemView.setOnClickListener(v -> {
                addFooterView();
            });
            return Unit.INSTANCE;
        });
        quickAdapterHelper.addAfterAdapter(footerAdapter);

        initData();
    }

    private void initData() {
        mAdapter.submitList(Arrays.asList("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"));
    }

    private void addHeaderView() {
        HeaderFooterAdapter headerAdapter = new HeaderFooterAdapter(R.layout.header_added);
        headerAdapter.setOnInitView(quickViewHolder -> {
            quickViewHolder.itemView.setOnClickListener(v -> {
                quickAdapterHelper.removeAdapter(headerAdapter);
            });
            return Unit.INSTANCE;
        });
        quickAdapterHelper.addBeforeAdapter(0, headerAdapter);
    }

    private void addFooterView() {
        HeaderFooterAdapter footerAdapter = new HeaderFooterAdapter(R.layout.footer_added);
        footerAdapter.setOnInitView(quickViewHolder -> {
            quickViewHolder.itemView.setOnClickListener(v -> {
                quickAdapterHelper.removeAdapter(footerAdapter);
            });
            return Unit.INSTANCE;
        });
        quickAdapterHelper.addAfterAdapter(footerAdapter);
    }
}
