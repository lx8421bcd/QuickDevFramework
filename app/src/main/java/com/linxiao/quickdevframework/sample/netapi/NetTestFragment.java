package com.linxiao.quickdevframework.sample.netapi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.net.CookieMode;
import com.linxiao.framework.net.RetrofitManager;
import com.linxiao.framework.net.HttpInfoCatchInterceptor;
import com.linxiao.framework.net.HttpInfoCatchListener;
import com.linxiao.framework.net.HttpInfoEntity;
import com.linxiao.quickdevframework.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetTestFragment extends BaseFragment {


    @BindView(R.id.tvResponse)
    TextView tvResponse;

    private ClientApi clientApi;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_net_test, container);
        ButterKnife.bind(this, getContentView());
        HttpInfoCatchInterceptor infoCatchInterceptor = new HttpInfoCatchInterceptor();
        infoCatchInterceptor.setCatchEnabled(true);
        infoCatchInterceptor.setHttpInfoCatchListener(new HttpInfoCatchListener() {
            @Override
            public void onInfoCaught(HttpInfoEntity entity) {
                entity.logOut();
                //do something......
            }
        });
        clientApi = RetrofitManager.createRetrofitBuilder("http://www.weather.com.cn/")
                .setCookieMode(CookieMode.ADD_BY_ANNOTATION)
                .addCustomInterceptor(infoCatchInterceptor)
                .build(ClientApi.class);
    }

    @OnClick(R.id.btnRequestNet)
    public void onRequestTestClick(View v) {
        requestApi();
    }

    public void requestApi() {
        Call<ResponseBody> call = clientApi.getWeather("101010100");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    tvResponse.setText("Response:\n " + response.body().string());
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
