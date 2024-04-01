package com.linxiao.quickdevframework.sample.widget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.widget.TextDrawable;
import com.linxiao.quickdevframework.databinding.FragmentWidgetsGuideBinding;
import com.linxiao.framework.architecture.SimpleViewBindingFragment;
import com.linxiao.quickdevframework.sample.mvvm.CaptchaActivity;

/**
 * Created by linxiao on 2017/2/12.
 */
public class WidgetsGuideFragment extends SimpleViewBindingFragment<FragmentWidgetsGuideBinding> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextDrawable drawable = TextDrawable.createRound();
        drawable.setBackgroundColor(Color.BLACK);
        drawable.setTextColor(Color.WHITE);
        drawable.setText("辣鸡");
        getViewBinding().ivTextDrawable.setImageDrawable(drawable);
        getViewBinding().btnMVVMSample.setOnClickListener(this::onMVVMSampleClick);
    }

    public void onMVVMSampleClick(View v) {
        startActivity(new Intent(getActivity(), CaptchaActivity.class));
    }
}
