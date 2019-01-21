package com.linxiao.framework.dialog;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.SpannedString;
import android.view.View;

import com.linxiao.framework.common.ScreenUtil;
import com.linxiao.framework.common.SpanFormatter;
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment;


/**
 * Fragment 基类
 * Created by linxiao on 2016-07-14.
 */
public abstract class BaseDialogFragment extends RxAppCompatDialogFragment {

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
     * get spanned string from xml resources
     * <p>use this method to get the text which include style labels in strings.xml,
     * support using format args</p>
     * @param resId string resource id
     * @param args format args
     * @return SpannedString
     */
    protected SpannedString getSpannedString(@StringRes int resId, Object... args) {
        return SpanFormatter.format(getText(resId), args);
    }
    
    protected int dp2px(float dpValue) {
        return ScreenUtil.dp2px(dpValue);
    }
    
    public static int px2dp(float pxValue) {
        return ScreenUtil.px2dp(pxValue);
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
