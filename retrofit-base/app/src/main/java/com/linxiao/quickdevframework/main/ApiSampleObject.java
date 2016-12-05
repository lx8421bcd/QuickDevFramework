package com.linxiao.quickdevframework.main;

/**
 *
 * Created by linxiao on 2016/11/30.
 */
public class ApiSampleObject {

    private String apiName;
    private Class<?> destActivity;

    public ApiSampleObject(String apiName, Class<?> destActivity) {
        this.apiName = apiName;
        this.destActivity = destActivity;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Class<?> getDestActivity() {
        return destActivity;
    }

    public void setDestActivity(Class<?> destActivity) {
        this.destActivity = destActivity;
    }
}
