package com.linxiao.framework.rx;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 默认订阅对象实现，可以有选择的实现方法
 * Created by linxiao on 2017/7/2.
 */
public class SampleSubscriber<T> implements Observer<T> {
    
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        
    }
    
    @Override
    public void onNext(@NonNull T t) {
        
    }
    
    @Override
    public void onError(@NonNull Throwable e) {
        
    }
    
    @Override
    public void onComplete() {
        
    }
}
