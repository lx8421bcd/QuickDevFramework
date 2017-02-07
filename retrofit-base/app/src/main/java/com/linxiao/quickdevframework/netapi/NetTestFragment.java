package com.linxiao.quickdevframework.netapi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.net.CookieMode;
import com.linxiao.framework.net.FrameworkRetrofitManager;
import com.linxiao.framework.net.HttpInfoCatchInterceptor;
import com.linxiao.framework.net.HttpInfoCatchListener;
import com.linxiao.framework.net.HttpInfoEntity;
import com.linxiao.quickdevframework.R;


import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetTestFragment extends BaseFragment {

    private ClientApi clientApi;

    @Override
    protected int getInflateLayoutRes() {
        return R.layout.fragment_net_test;
    }

    @Override
    protected void onCreateContentView(View contentView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);
        HttpInfoCatchInterceptor infoCatchInterceptor = new HttpInfoCatchInterceptor();
        infoCatchInterceptor.setCatchEnabled(true);
        infoCatchInterceptor.setHttpInfoCatchListener(new HttpInfoCatchListener() {
            @Override
            public void onInfoCaught(HttpInfoEntity entity) {
                entity.logOut();
                //do something......
            }
        });
        clientApi = FrameworkRetrofitManager.createRetrofitBuilder("http://www.weather.com.cn/")
        .setCookieMode(CookieMode.ADD_BY_ANNOTATION)
        .addCustomInterceptor(infoCatchInterceptor)
        .build(ClientApi.class);
    }

    @OnClick(R.id.btnRequestNet)
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
