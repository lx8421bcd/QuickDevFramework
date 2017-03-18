package com.linxiao.framework.support.file;

/**
 * 文件大小监听
 * Created by lbc on 2017/3/11.
 */

public interface FileSizeListener {
    void onStart();

    void onProgressUpdate(double count, double current);

    void onSuccess();

    void onFail(String failMsg);
}
