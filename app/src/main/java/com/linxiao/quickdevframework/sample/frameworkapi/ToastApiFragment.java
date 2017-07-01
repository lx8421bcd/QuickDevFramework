package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.toast.ToastWrapper;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ToastApiFragment extends BaseFragment {

    int toastNum = 1;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_toast_api, container);
        ButterKnife.bind(this, getContentView());
    }

    @OnClick(R.id.btnShowToast)
    public void onShowToastClick(View v) {
        ToastWrapper.showToast(getContext(), "toast " + toastNum++);
//        ToastWrapper.showToast(getContext(), "toast ", 10000);
    }

}
