package com.linxiao.framework.file;

/**
 * 根据文件数量监听
 * Created by lbc on 2017/3/11.
 */

public interface FileCountListener {
    void onStart();

    void onProgressUpdate(long count, long current);

    void onSuccess();

    void onFail(String failMsg);
}
