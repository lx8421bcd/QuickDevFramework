package com.linxiao.quickdevframework.sample.netapi;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.linxiao.framework.rx.SampleSubscriber;
import com.linxiao.framework.toast.ToastAlert;
import com.linxiao.quickdevframework.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCustomInterceptor(infoCatchInterceptor)
                .build(ClientApi.class);
    }

    @OnClick(R.id.btnRequestNet)
    public void onRequestTestClick(View v) {
        requestApi();
    }

    public void requestApi() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        clientApi.getWeather("101010100")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(new Function<ResponseBody, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull ResponseBody responseBody) throws Exception {
                return Observable.just(responseBody.string());
            }
        })
        .doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                progressDialog.setMessage("正在请求");
                progressDialog.show();
            }
        })
        .doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                progressDialog.dismiss();
            }
        })
        .subscribe(new SampleSubscriber<String>(){
    
            @Override
            public void onNext(@NonNull String responseBody) {
                String result = "Response:\n " + responseBody;
                tvResponse.setText(result);
            }
        });
    }

}
