package com.linxiao.quickdevframework.sample.mvvm;

import com.linxiao.framework.architecture.BaseActivity;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * MVVM架构Activity基类
 * <p> class description </p>
 *
 * @author linxiao
 * @since 2018/3/9.
 */

public class BaseMVVMActivity extends BaseActivity {
    
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    
    /**
     * subscribe data which provide by ViewModel
     * <p>observed data will unsubscribe while Activity onStop,
     * for re-subscribe data correctly, suggest perform observe
     * data in method {@link #onStart()}</p>
     * */
    protected void observe(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }
    
    
    /**
     * used to add data binding in mvvm.
     * <p>subscribe to the data source provided in ViewModel here </p>
     * */
    protected void onCreateDataBinding() {
        //add data binding
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        onCreateDataBinding();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
