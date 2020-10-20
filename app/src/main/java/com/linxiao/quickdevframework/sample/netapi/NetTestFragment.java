package com.linxiao.quickdevframework.sample.netapi;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.linxiao.framework.rx.RxSubscriber;
import com.linxiao.quickdevframework.databinding.FragmentNetTestBinding;
import com.linxiao.quickdevframework.main.SimpleViewBindingFragment;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NetTestFragment extends SimpleViewBindingFragment<FragmentNetTestBinding> {
    
    NetTestDataManager mDataManager;

    @Override
    public void onViewCreated(@androidx.annotation.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataManager = new NetTestDataManager();
        getViewBinding().btnRequestNet.setOnClickListener(this::onRequestTestClick);
    }

    public void onRequestTestClick(View v) {
        requestApi();
    }

    public void requestApi() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        mDataManager.getTestData()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                progressDialog.setMessage("正在请求");
                progressDialog.show();
            }
        })
        .doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                progressDialog.dismiss();
            }
        })
        .subscribe(new RxSubscriber<String>(){
    
            @Override
            public void onNext(@NonNull String responseBody) {
                String result = "Response:\n " + responseBody;
                getViewBinding().tvResponse.setText(result);
            }
        });
    }

}
