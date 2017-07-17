package com.linxiao.quickdevframework.sample.mvvm;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.linxiao.framework.activity.BaseDataBindingActivity;
import com.linxiao.framework.toast.ToastAlert;
import com.linxiao.quickdevframework.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CaptchaActivity extends BaseDataBindingActivity {
    
    @BindView(R.id.btn_request_captcha)
    Button btnRequestCaptcha;
    
    @BindView(R.id.et_mobile)
    EditText etMobile;
    
    CaptchaViewModel captchaViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);
        ButterKnife.bind(this);
        captchaViewModel = ViewModelProviders.of(this).get(CaptchaViewModel.class);
        
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
        observe(captchaViewModel.captchaCountDown()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer remains) throws Exception {
                if (remains > 0) {
                    btnRequestCaptcha.setText(String.valueOf(remains));
                }
                else {
                    btnRequestCaptcha.setText("request");
                    updateCaptchaState(etMobile.getText().toString());
                }
                
            }
        }));
        observe(captchaViewModel.canRequestCaptcha()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean canRequest) throws Exception {
                btnRequestCaptcha.setEnabled(canRequest);
            }
        }));
    }
    
    private void updateCaptchaState(String mobile) {
        captchaViewModel.checkRequestEnabled(mobile);
    }
    
    @OnClick(R.id.btn_request_captcha)
    void onRequestCaptchaClick(View v) {
        captchaViewModel.requestSMSCaptcha(etMobile.getText().toString())
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
