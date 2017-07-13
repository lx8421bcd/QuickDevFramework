package com.linxiao.quickdevframework.sample.mvvm;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.linxiao.framework.net.SessionManager;
import com.linxiao.framework.util.RegexUtil;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 用户ViewModel
 * <p></p>
 * TODO 添加被顶掉线监听
 * Created by linxiao on 2017/7/11.
 */
public class CaptchaViewModel extends ViewModel {
    
    private static final int COUNT_DOWN_SECONDS = 60;
    
    
    private String cachedMobile;
    private int remain;
    
    public CaptchaViewModel() {
    }
    
    /**
     * 获取验证码请求状态
     * */
    public Flowable<Integer> captchaRequestState() {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Boolean> e) throws Exception {
                Log.d("CaptchaModel", "changeRequest: " + cachedMobile);
                if (remain > 0) { //上次倒计时没完
                    e.onComplete();
                    return;
                }
                if (!RegexUtil.checkPhoneNumberLegality(cachedMobile)) {
                    e.onComplete();
                    return;
                }
                e.onNext(true);
            }
        }, BackpressureStrategy.BUFFER)
        .flatMap(new Function<Boolean, Publisher<Long>>() {
            @Override
            public Publisher<Long> apply(@NonNull Boolean canReq) throws Exception {
                if (canReq) {
                    if (remain <= 0) {
                        remain = COUNT_DOWN_SECONDS;
                    }
                    return Flowable.interval(0, 1, TimeUnit.SECONDS);
                }
                return Flowable.just(-1L);
            }
        })
        .map(new Function<Long, Integer>() {
            @Override
            public Integer apply(@NonNull Long seconds) throws Exception {
                if (seconds < 0) {
                    return seconds.intValue();
                }
                remain -= 1;
                return remain;
            }
        });
                
    }
    
    /**
     * 检查手机号
     * */
    public Flowable<Boolean> checkMobile(final String mobile) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Boolean> e) throws Exception {
                Log.d("CaptchaModel", "mobile: " + mobile);
                if (RegexUtil.checkPhoneNumberLegality(mobile)) {
                    cachedMobile = mobile;
                    e.onNext(true);
                    e.onComplete();
                    return;
                }
                e.onNext(false);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }
    
    /**
     * 请求短信验证码
     * */
    public Flowable<Void> requestSMSCaptcha(String mobile) {
        
        return null;
    }
}
