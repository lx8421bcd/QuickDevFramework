package com.linxiao.quickdevframework.netapi;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.net.RetrofitApiManager;
import com.linxiao.quickdevframework.R;


import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetTestActivity extends BaseActivity {

    private ClientApi clientApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_test);
//        clientApi = RetrofitApiManager.buildClientAPI()
//                .setServerUrl("http://www.weather.com.cn/")
//                .addConvertFactory(GsonConverterFactory.create())
//                .build(ClientApi.class);


    }

    public void onRequestTestClick(View v) {
        requestApi();
    }

    public void requestApi() {
        Call<ResponseBody> call =  clientApi.getWeather("101010100");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d(TAG, "onResponse: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
