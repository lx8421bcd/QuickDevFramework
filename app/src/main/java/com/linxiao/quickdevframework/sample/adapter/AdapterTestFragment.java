package com.linxiao.quickdevframework.sample.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdapterTestFragment extends BaseFragment {

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_adapter_test, container);
        ButterKnife.bind(this, getContentView());
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
