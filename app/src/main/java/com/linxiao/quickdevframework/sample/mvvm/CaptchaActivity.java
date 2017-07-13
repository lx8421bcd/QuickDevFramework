package com.linxiao.quickdevframework.sample.mvvm;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.linxiao.framework.activity.BaseDataBindingActivity;
import com.linxiao.framework.log.Logger;
import com.linxiao.quickdevframework.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CaptchaActivity extends BaseDataBindingActivity {
    
    private Button btnRequestCaptcha;
    private EditText etMobile;
    
    CaptchaViewModel captchaViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);
        captchaViewModel = ViewModelProviders.of(this).get(CaptchaViewModel.class);
        
        btnRequestCaptcha = findView(R.id.btn_request_captcha);
        etMobile = findView(R.id.et_mobile);
        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCaptchaState(s.toString());
            }
    
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        observe(captchaViewModel.captchaRequestState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer seconds) throws Exception {
                Logger.d(TAG, "accpet : " + seconds);
                if (seconds > 0) {
                    btnRequestCaptcha.setEnabled(false);
                    btnRequestCaptcha.setText(String.valueOf(seconds));
                }
                else if (seconds == -1) {
                    btnRequestCaptcha.setText("request");
                    btnRequestCaptcha.setEnabled(false);
                }
                else {
                    btnRequestCaptcha.setText("request");
                    btnRequestCaptcha.setEnabled(true);
                }
            }
        }));
    }
    
    private void updateCaptchaState(String mobile) {
        observe(captchaViewModel.checkMobile(mobile)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Boolean>() {
           @Override
           public void accept(@NonNull Boolean aBoolean) throws Exception {
                
           }
        }));
    }
}
