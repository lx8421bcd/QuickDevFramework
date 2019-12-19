package com.linxiao.framework.file;

/**
 * file async operate callback listener
 *
 * @author lbc,linxiao
 * @since 2017-03-11
 */
public interface FileModifyListener {
    void onStart();

    void onProgressUpdate(long totalCount, long finishedCount, long totalSize, long finishedSize);

    void onSuccess();

    void onError(Throwable e);
}
