package com.linxiao.framework.support.file;

/**
 * 根据文件数量监听
 * Created by lbc on 2017/3/11.
 */

public interface FileSumListener {
    void onStart();

    void onProgressUpdate(long count, long current);

    void onSuccess();

    void onFail(String failMsg);
}
