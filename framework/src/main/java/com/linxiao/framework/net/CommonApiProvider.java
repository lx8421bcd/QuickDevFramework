package com.linxiao.framework.net;

public class CommonApiProvider extends RetrofitApiProvider<CommonApi> {

    private static CommonApiProvider instance;

    public static CommonApiProvider getInstance() {
        if (instance == null) {
            instance = new CommonApiProvider();
        }
        return instance;
    }

    @Override
    protected String getApiBaseUrl() {
        return "useless.host";
    }

    @Override
    protected Class<CommonApi> getApiClass() {
        return CommonApi.class;
    }
}
