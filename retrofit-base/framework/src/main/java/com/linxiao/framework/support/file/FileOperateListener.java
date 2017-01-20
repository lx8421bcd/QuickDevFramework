package com.linxiao.framework.support.file;

/**
 * 文件操作接口，用于异步文件操作
 * Created by linxiao on 2017/1/20.
 */
public interface FileOperateListener {

    void onStart();

    void onProgressUpdate();

    void onSuccess();

    boolean onFail();

}
