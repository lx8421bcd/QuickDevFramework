package com.linxiao.quickdevframework.frameworkapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogApiFragment extends BaseFragment {

    @Override
    protected int getInflateLayoutRes() {
        return R.layout.fragment_dialog_api;
    }

    @Override
    protected void onCreateContentView(View contentView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick(R.id.btnShowSimpleDialog)
    public void onSimpleDialogClick(View v) {
        AlertDialogWrapper.showAlertDialog("simple dialog messages");
    }

    @OnClick(R.id.btnShowTopDialog)
    public void onShowTopDialogClick(View v) {
        Intent backServiceIntent = new Intent(getActivity(), BackgroundService.class);
        getActivity().startService(backServiceIntent);
    }

    @OnClick(R.id.btnShowBottomDialog)
    public void onShowBottomDialogClick(View v) {
        SampleBottomDialogFragment dialogFragment = new SampleBottomDialogFragment();
        dialogFragment.show(getFragmentManager(), "SampleDialog");
    }
}
