package com.linxiao.framework.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 用于MVVM架构数据绑定的Activity
 * Created by linxiao on 2017/7/12.
 */
public abstract class BaseDataBindingActivity extends BaseActivity {
    
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    protected void observe(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
    }
}
