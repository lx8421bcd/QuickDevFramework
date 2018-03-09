package com.linxiao.quickdevframework.sample.mvvm;

import com.linxiao.framework.activity.BaseActivity;

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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
