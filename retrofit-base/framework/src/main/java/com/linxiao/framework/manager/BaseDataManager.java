package com.linxiao.framework.manager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * base data manager class
 * Created by LinXiao on 2016-11-24.
 */
public abstract class BaseDataManager {

    protected static String TAG;

    /**
     * Retrofit Calls的缓存容器
     * */
    private List<Call> listApiCalls;

    public BaseDataManager() {
        TAG = this.getClass().getSimpleName();
        listApiCalls = new ArrayList<>();
    }

    /**
     * 取消所有DataManager正在进行的网络请求。
     * */
    public void cancelAllCalls() {
        for (Call call : listApiCalls) {
            if(call == null) {
                continue;
            }
            if (call.isExecuted() && !call.isCanceled()) {
                call.cancel();
            }
        }
    }

    /**
     * 将Retrofit的Call绑定到DataManager上，在UI层调用Cancel时，取消请求
     * */
    protected void bindCallToManager(Call call) {
        listApiCalls.add(call);
    }

    /**
     * 解除Retrofit的Call与DataManager的绑定
     * @param call 需要解除绑定的call
     * */
    protected void unBindCall(Call call) {
        listApiCalls.remove(call);
    }
}
