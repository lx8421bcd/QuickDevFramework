package com.linxiao.framework.manager;

/**
 * UI层向DataManager发起请求的回调接口
 * Created by linxiao on 2017/2/4.
 */
public interface ActionListener<T> {

    void onSuccess(T data);

    void onFailure(Throwable t, String message);

}
