package com.linxiao.quickdevframework.sample.mvvm

import com.linxiao.framework.architecture.BaseActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * MVVM架构Activity基类
 *
 *  class description
 *
 * @author linxiao
 * @since 2018-03-09.
 */
open class BaseMVVMActivity : BaseActivity() {
    private val mCompositeDisposable = CompositeDisposable()

    /**
     * subscribe data which provide by ViewModel
     *
     * observed data will unsubscribe while Activity onStop,
     * for re-subscribe data correctly, suggest perform observe
     * data in method [.onStart]
     */
    protected fun observe(disposable: Disposable?) {
        mCompositeDisposable.add(disposable!!)
    }

    /**
     * used to add data binding in mvvm.
     *
     * subscribe to the data source provided in ViewModel here
     */
    protected fun onCreateDataBinding() {
        //add data binding
    }

    override fun onStart() {
        super.onStart()
        onCreateDataBinding()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}
