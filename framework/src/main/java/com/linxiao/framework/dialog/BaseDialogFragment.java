package com.linxiao.framework.dialog;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.Toast;

import com.linxiao.framework.manager.BaseDataManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment 基类
 * Created by LinXiao on 2016-07-14.
 */
public abstract class BaseDialogFragment extends AppCompatDialogFragment {

    public static String TAG;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * use this method instead of findViewById() to simplify view initialization <br>
     * it's not unchecked because T extends View
     * */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View layoutView, @IdRes int resId) {
        return (T) layoutView.findViewById(resId);
    }

}
