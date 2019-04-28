package com.linxiao.framework.rx;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
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
    public static Observable<Integer> countDown(final int seconds) {
        return Observable.interval(0, 1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.computation())
        .take(seconds)
        .map(increaseTime -> seconds - increaseTime.intValue());
    }
    
    
}
