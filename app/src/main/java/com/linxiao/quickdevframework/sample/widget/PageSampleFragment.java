package com.linxiao.quickdevframework.sample.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.quickdevframework.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by linxiao on 2017/2/12.
 */
public class PageSampleFragment extends BaseFragment {

    @BindView(R.id.tvPageDesc)
    TextView tvPageDesc;

    String pageDesc;

    @Override
    protected int rootViewResId() {
        return R.layout.fragment_page_sample;
    }

    @Override
    protected void onCreateContentView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
        tvPageDesc.setText(pageDesc);
    }

    public void setPageDesc(String desc) {
        pageDesc = desc;
    }
}
