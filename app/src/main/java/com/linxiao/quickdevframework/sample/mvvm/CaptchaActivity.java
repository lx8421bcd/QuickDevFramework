package com.linxiao.quickdevframework.sample.mvvm;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.lifecycle.ViewModelProviders;

import com.linxiao.framework.common.ToastAlert;
import com.linxiao.quickdevframework.databinding.ActivityCaptchaBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CaptchaActivity extends BaseMVVMActivity {
    
    ActivityCaptchaBinding binding;
    CaptchaViewModel captchaViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaptchaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        captchaViewModel = ViewModelProviders.of(this).get(CaptchaViewModel.class);

        binding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCaptchaState(s.toString());
            }
    
            @Override
            public void afterTextChanged(Editable s) {}
        });
        binding.btnRequestCaptcha.setOnClickListener(this::onRequestCaptchaClick);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        observe(captchaViewModel.captchaCountDown()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer remains) throws Exception {
                if (remains > 0) {
                    binding.btnRequestCaptcha.setText(String.valueOf(remains));
                }
                else {
                    binding.btnRequestCaptcha.setText("request");
                    updateCaptchaState(binding.etMobile.getText().toString());
                }
                
            }
        }));
        observe(captchaViewModel.canRequestCaptcha()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean canRequest) throws Exception {
                binding.btnRequestCaptcha.setEnabled(canRequest);
            }
        }));
    }
    
    private void updateCaptchaState(String mobile) {
        captchaViewModel.checkRequestEnabled(mobile);
    }
    
    void onRequestCaptchaClick(View v) {
        captchaViewModel.requestSMSCaptcha(binding.etMobile.getText().toString())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean result) throws Exception {
                String strResult = result ? "请求成功" : "请求失败";
                ToastAlert.showToast(CaptchaActivity.this, strResult);
            }
        });
    }
}
