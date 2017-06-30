package com.linxiao.quickdevframework.sample.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdapterTestFragment extends BaseFragment {

    @Override
    protected int rootViewResId() {
        return R.layout.fragment_adapter_test;
    }

    @Override
    protected void onCreateContentView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
    }

    @OnClick(R.id.btnTestEmptyView)
    public void onTestEmptyClick(View v) {
        startActivity(new Intent(getActivity(), EmptyTestActivity.class));
    }

    @OnClick(R.id.btnHeaderFooter)
    public void onHeaderFooterClick(View v) {
        startActivity(new Intent(getActivity(), HeaderFooterActivity.class));
    }

}
