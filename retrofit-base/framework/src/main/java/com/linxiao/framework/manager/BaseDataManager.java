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

    private List<Call> listApiCalls;

    public BaseDataManager() {
        TAG = this.getClass().getSimpleName();
        listApiCalls = new ArrayList<>();
    }

    protected void bindCallToManager(Call call) {
        listApiCalls.add(call);
    }

    public void cancelAllCalls() {
        for (Call call : listApiCalls) {
            if (call.isExecuted() && !call.isCanceled()) {
                call.cancel();
            }
        }
    }
}
