package com.linxiao.framework.net;

/**
 * Http请求测速回调
 * Created by LinXiao on 2017-01-05.
 */
public interface HttpInfoCatchListener {

    void onInfoCaught(HttpInfoEntity entity);
}
