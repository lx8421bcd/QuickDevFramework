package com.linxiao.quickdevframework.sample.frameworkapi;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.framework.common.ApplicationUtil;
import com.linxiao.framework.common.ContextProvider;
import com.linxiao.quickdevframework.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    @BindView(R.id.ivAppIcon)
    ImageView ivAppIcon;
    @BindView(R.id.tvIsAppRunning)
    TextView tvIsAppRunning;
    @BindView(R.id.tvIsAppForeground)
    TextView tvIsAppForeground;
    @BindView(R.id.tvSystemBootTime)
    TextView tvSystemBootTime;
    @BindView(R.id.tvCPUName)
    TextView tvCPUName;


    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_application_api, container);
        ButterKnife.bind(this, getContentView());

        ivAppIcon.setImageDrawable(ApplicationUtil.getAppIcon(ContextProvider.get().getPackageName()));
        tvIsAppRunning.setText(getString(R.string.is_app_running) + ": " + ApplicationUtil.isAppForeground());
        tvIsAppForeground.setText(getString(R.string.is_app_foreground) + ": " + ApplicationUtil.isAppForeground());
        tvCPUName.setText("CPU Name: " + ApplicationUtil.getCPUName());
    }

    @OnClick(R.id.btnGetAppName)
    public void onGetAppNameClick(View v) {
        tvAppName.setText(ApplicationUtil.getAppName(ContextProvider.get().getPackageName()));
    }

    @OnClick(R.id.btnGetAppVersion)
    public void onGetAppVersionClick(View v) {
        PackageInfo info = ApplicationUtil.getPackageInfo(ContextProvider.get().getPackageName());
        if (info != null) {
            tvAppVersion.setText(info.versionName);
        }
    }

    @OnClick(R.id.btnExitApp)
    public void onExitAppClick(View v) {
        ApplicationUtil.exitApplication(getActivity());
    }

    @OnClick(R.id.btnRestartApp)
    public void onRestartAppClick(View v) {
        ApplicationUtil.restartApplication(getActivity());
    }

    @OnClick(R.id.btnGetSystemBootTime)
    public void onSystemBootTimeClick(View v) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault());

        tvSystemBootTime.setText(format.format(new Date(ApplicationUtil.getSystemBootTime())));
    }
}
