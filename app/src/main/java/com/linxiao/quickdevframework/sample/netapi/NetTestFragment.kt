package com.linxiao.quickdevframework.sample.netapi

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.rx.RxSubscriber
import com.linxiao.quickdevframework.databinding.FragmentNetTestBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NetTestFragment : SimpleViewBindingFragment<FragmentNetTestBinding>() {

    private var mDataManager: NetTestDataManager = NetTestDataManager()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnRequestNet.setOnClickListener { v: View? -> onRequestTestClick(v) }
    }

    fun onRequestTestClick(v: View?) {
        requestApi()
    }

    fun requestApi() {
        val progressDialog = ProgressDialog(context)
        mDataManager.testData
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
            progressDialog.setMessage("正在请求")
            progressDialog.show()
        }
        .doOnComplete { progressDialog.dismiss() }
        .doOnNext {
            val result = "Response:\n $it"
            viewBinding.tvResponse.text = result
        }
        .subscribe(RxSubscriber())
    }
}
