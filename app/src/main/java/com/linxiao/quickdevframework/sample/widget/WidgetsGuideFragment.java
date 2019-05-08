package com.linxiao.quickdevframework.sample.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.sample.mvvm.CaptchaActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * Created by linxiao on 2017/2/12.
 */
public class WidgetsGuideFragment extends BaseFragment {
    

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_widgets_guide, container);
        ButterKnife.bind(this, getContentView());
    }
    
    @OnClick(R.id.btnMVVMSample)
    public void onMVVMSampleClick(View v) {
        startActivity(new Intent(getActivity(), CaptchaActivity.class));
    }
}
