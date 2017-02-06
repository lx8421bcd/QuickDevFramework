package com.linxiao.quickdevframework.main;

/**
 *
 * Created by linxiao on 2016/11/30.
 */
public class ApiSampleObject {

    private String apiName;
    private String target;

    public ApiSampleObject(String apiName, String target) {
        this.apiName = apiName;
        this.target = target;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
