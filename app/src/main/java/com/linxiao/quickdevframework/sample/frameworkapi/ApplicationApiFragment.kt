package com.linxiao.quickdevframework.sample.frameworkapi;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.common.ApplicationUtil;

import com.linxiao.framework.common.ContextProviderKt;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.FragmentApplicationApiBinding;
import com.linxiao.framework.architecture.SimpleViewBindingFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Application类提供API示例
 * Created by linxiao on 2017/2/17.
 */
public class ApplicationApiFragment extends SimpleViewBindingFragment<FragmentApplicationApiBinding> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().ivAppIcon.setImageDrawable(ApplicationUtil.getAppIcon(ContextProviderKt.getGlobalContext().getPackageName()));
        getViewBinding().tvIsAppRunning.setText(getString(R.string.is_app_running) + ": " + ApplicationUtil.isAppForeground());
        getViewBinding().tvIsAppForeground.setText(getString(R.string.is_app_foreground) + ": " + ApplicationUtil.isAppForeground());
        getViewBinding().tvCPUName.setText("CPU Name: " + ApplicationUtil.getCPUName());
        getViewBinding().btnGetAppName.setOnClickListener(v -> {
            getViewBinding().tvAppName.setText(ApplicationUtil.getAppName(ContextProviderKt.getGlobalContext().getPackageName()));
        });
        getViewBinding().btnGetAppVersion.setOnClickListener(v -> {
            PackageInfo info = ApplicationUtil.getPackageInfo(ContextProviderKt.getGlobalContext().getPackageName());
            if (info != null) {
                getViewBinding().tvAppVersion.setText(info.versionName);
            }
        });
        getViewBinding().btnExitApp.setOnClickListener(v -> {
            ApplicationUtil.exitApplication(getActivity());
        });
        getViewBinding().btnRestartApp.setOnClickListener(v -> {
            ApplicationUtil.restartApplication(getActivity());
        });
        getViewBinding().btnGetSystemBootTime.setOnClickListener(v -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault());
            getViewBinding().tvSystemBootTime.setText(format.format(new Date(ApplicationUtil.getSystemBootTime())));
        });
    }

}
