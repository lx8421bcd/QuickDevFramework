package com.linxiao.quickdevframework.sample.mvvm;

import androidx.lifecycle.ViewModel;

import com.linxiao.framework.common.RegexUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

/**
 *
 * Created by linxiao on 2017/7/11.
 */
public class CaptchaViewModel extends ViewModel {
    
    private static final int COUNT_DOWN_SECONDS = 60;
    
    
    private String cachedMobile;
    private int remain = 0;
    
    private BehaviorSubject<String> subjectCaptchaCountDown = BehaviorSubject.create();
    private BehaviorSubject<Boolean> subjectCanRequestCaptcha = BehaviorSubject.create();
    
    public CaptchaViewModel() {
        
    }
    
    
    
    /**
     * 获取验证码请求状态
     * */
    public Observable<Integer> captchaCountDown() {
        return subjectCaptchaCountDown
        .flatMap(new Function<String, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(@NonNull String integer) throws Exception {
                if (remain <= 0) {
                    remain = COUNT_DOWN_SECONDS;
                }
                return Observable.interval(0, 1, TimeUnit.SECONDS)
                        .take(COUNT_DOWN_SECONDS);
            }
        })
        .map(new Function<Long, Integer>() {
            @Override
            public Integer apply(@NonNull Long seconds) throws Exception {
                remain -= 1;
                return remain;
            }
        });
    }
    
    public Observable<Boolean> canRequestCaptcha() {
        return subjectCanRequestCaptcha;
    }
    
    /**
     * 检查是否能够请求验证码
     * <p>不能够请求的情况：           <br>
     * 1. 手机号码不符合              <br>
     * 2. 已经请求过验证码正在CD       <br>
     *     </p>
     * */
    public void checkRequestEnabled(String mobile) {
        if (remain > 0) { //上次倒计时没完
            subjectCanRequestCaptcha.onNext(false);
            return;
        }
        if (!RegexUtil.isValidPhoneNum(mobile)) {
            subjectCanRequestCaptcha.onNext(false);
            return;
        }
        subjectCanRequestCaptcha.onNext(true);
    }
    
    /**
     * 请求短信验证码
     * */
    public Observable<Boolean> requestSMSCaptcha(final String mobile) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                if (RegexUtil.isValidPhoneNum(mobile)) {
                    e.onNext(true);
                    e.onComplete();
                    return;
                }
                e.onNext(false);
                e.onComplete();
            }
        })
        //这一步是根据上面的检查判断是否请求验证码，之后再将请求结果转为Boolean，这里省略，直接转Boolean
        .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(@NonNull Boolean request) throws Exception {
                if (request) {
                    cachedMobile = mobile;
                    subjectCaptchaCountDown.onNext(mobile);
                    subjectCanRequestCaptcha.onNext(false);
                }
                return Observable.just(request);
            }
        });
    }
}
