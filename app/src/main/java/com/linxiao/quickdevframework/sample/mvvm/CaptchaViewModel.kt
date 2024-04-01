package com.linxiao.quickdevframework.sample.mvvm

import androidx.lifecycle.ViewModel
import com.linxiao.framework.common.RegexUtil.isValidPhoneNum
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 *
 * @author lx8421bcd
 * @since 2017-07-11
 */
class CaptchaViewModel : ViewModel() {

    companion object {
        private const val COUNT_DOWN_SECONDS = 60
    }

    private var cachedMobile: String? = null
    private var remain = 0
    private val subjectCaptchaCountDown = BehaviorSubject.create<String>()
    private val subjectCanRequestCaptcha = BehaviorSubject.create<Boolean>()

    /**
     * 获取验证码请求状态
     */
    fun captchaCountDown(): Observable<Int> {
        return subjectCaptchaCountDown
            .flatMap {
                if (remain <= 0) {
                    remain = COUNT_DOWN_SECONDS
                }
                Observable.interval(0, 1, TimeUnit.SECONDS)
                    .take(COUNT_DOWN_SECONDS.toLong())
            }
            .map {
                remain -= 1
                remain
            }
    }

    fun canRequestCaptcha(): Observable<Boolean> {
        return subjectCanRequestCaptcha
    }

    /**
     * 检查是否能够请求验证码
     *
     * 不能够请求的情况：           <br></br>
     * 1. 手机号码不符合              <br></br>
     * 2. 已经请求过验证码正在CD       <br></br>
     *
     */
    fun checkRequestEnabled(mobile: String?) {
        if (remain > 0) { //上次倒计时没完
            subjectCanRequestCaptcha.onNext(false)
            return
        }
        if (!isValidPhoneNum(mobile)) {
            subjectCanRequestCaptcha.onNext(false)
            return
        }
        subjectCanRequestCaptcha.onNext(true)
    }

    /**
     * 请求短信验证码
     */
    fun requestSMSCaptcha(mobile: String): Observable<Boolean> {
        return Observable.create(ObservableOnSubscribe { e ->
            if (isValidPhoneNum(mobile)) {
                e.onNext(true)
                e.onComplete()
                return@ObservableOnSubscribe
            }
            e.onNext(false)
            e.onComplete()
        }) //这一步是根据上面的检查判断是否请求验证码，之后再将请求结果转为Boolean，这里省略，直接转Boolean
        .flatMap { request ->
            if (request) {
                cachedMobile = mobile
                subjectCaptchaCountDown.onNext(mobile)
                subjectCanRequestCaptcha.onNext(false)
            }
            Observable.just(request)
        }
    }
}
