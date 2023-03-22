package com.linxiao.framework.rx;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


/**
 * RxJava 工具类
 * <p>提供RxJava相关常用工具方法</p>
 * Created by linxiao on 2016-07-24.
 */
public class RxJavaUtil {
    
    /**
     * 倒计时，单位 s，返回回调在主线程
     * @param seconds 倒计时秒数
     * */
    public static Observable<Integer> countdown(final int seconds) {
        return Observable.interval(0, 1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.newThread())
        .take(seconds + 1)
        .map(increaseTime -> seconds - increaseTime.intValue());
    }
    
    
}
