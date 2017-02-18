package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.SampleApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Application类提供API示例
 * Created by linxiao on 2017/2/17.
 */
public class ApplicationApiFragment extends BaseFragment {

    @BindView(R.id.tvAppName)
    TextView tvAppName;
    @BindView(R.id.tvAppVersion)
    TextView tvAppVersion;
    @BindView(R.id.tvAppSignature)
    TextView tvAppSignature;
    @BindView(R.id.ivAppIcon)
    ImageView ivAppIcon;
    @BindView(R.id.tvIsAppRunning)
    TextView tvIsAppRunning;
    @BindView(R.id.tvIsAppForeground)
    TextView tvIsAppForeground;

    @Override
    protected int rootViewResId() {
        return R.layout.fragment_application_api;
    }

    @Override
    protected void onCreateContentView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);

        ivAppIcon.setImageDrawable(SampleApplication.getApplicationIcon());
        tvIsAppRunning.setText(getString(R.string.is_app_running) + ": " + SampleApplication.isAppRunning());
        tvIsAppForeground.setText(getString(R.string.is_app_foreground) + ": " + SampleApplication.isAppForeground());
    }

    @OnClick(R.id.btnGetAppName)
    public void onGetAppNameClick(View v) {
        tvAppName.setText(SampleApplication.getApplicationName());
    }

    @OnClick(R.id.btnGetAppVersion)
    public void onGetAppVersionClick(View v) {
        tvAppVersion.setText(SampleApplication.getApplicationVersion());
    }

    @OnClick(R.id.btnGetAppSignature)
    public void onGetAppSignatureClick(View v) {
        tvAppSignature.setText(SampleApplication.getAppSignature());
    }

    @OnClick(R.id.btnExitApp)
    public void onExitAppClick(View v) {
        SampleApplication.exitApplication();
    }
}
