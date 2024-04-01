package com.linxiao.quickdevframework.sample.mvvm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.linxiao.framework.common.ToastAlert.showToast
import com.linxiao.framework.rx.RxSubscriber
import com.linxiao.quickdevframework.databinding.ActivityCaptchaBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CaptchaActivity : BaseMVVMActivity() {
    
    private lateinit var binding: ActivityCaptchaBinding
    private lateinit var captchaViewModel: CaptchaViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptchaBinding.inflate(
            layoutInflater
        )
        setContentView(binding.getRoot())
        captchaViewModel = ViewModelProvider(this).get(CaptchaViewModel::class.java)
        binding.etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateCaptchaState(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.btnRequestCaptcha.setOnClickListener { v: View? -> onRequestCaptchaClick(v) }
    }

    override fun onStart() {
        super.onStart()
        observe(
            captchaViewModel.captchaCountDown()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { remains: Int ->
                if (remains > 0) {
                    binding.btnRequestCaptcha.text = remains.toString()
                } else {
                    binding.btnRequestCaptcha.text = "request"
                    updateCaptchaState(binding.etMobile.getText().toString())
                }
            }
        )
        observe(
            captchaViewModel.canRequestCaptcha()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { canRequest: Boolean? ->
                binding.btnRequestCaptcha.setEnabled(
                    canRequest!!
                )
            }
        )
    }

    private fun updateCaptchaState(mobile: String) {
        captchaViewModel.checkRequestEnabled(mobile)
    }

    fun onRequestCaptchaClick(v: View?) {
        captchaViewModel.requestSMSCaptcha(binding.etMobile.getText().toString())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext { result: Boolean ->
            val strResult = if (result) "请求成功" else "请求失败"
            showToast(this@CaptchaActivity, strResult)
        }
        .subscribe(RxSubscriber())
    }
}
