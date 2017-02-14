package com.linxiao.quickdevframework.sample.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by linxiao on 2017/2/12.
 */
public class WidgetsGuideFragment extends BaseFragment {

    @Override
    protected int rootViewResId() {
        return R.layout.fragment_widgets_guide;
    }

    @Override
    protected void onCreateContentView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
    }

    @OnClick(R.id.btnViewPager)
    public void onCusViewPagerClick(View v) {
        startActivity(new Intent(getActivity(), ViewPagerActivity.class));
    }
}
