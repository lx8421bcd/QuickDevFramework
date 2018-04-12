package com.linxiao.framework.rx;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * RxJava 可取消订阅的订阅类
 * <p>默认订阅对象实现，可以有选择的实现方法</p>
 *
 * Created by linxiao on 2017/7/2.
 */
public class RxSubscriber<T> extends DisposableObserver<T> {
    
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
