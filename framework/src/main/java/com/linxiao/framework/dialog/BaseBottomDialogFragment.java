package com.linxiao.framework.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.linxiao.framework.R;

/**
 * 从底部显示的全屏Dialog基类
 * Created by linxiao on 2016-12-12.
 */
public abstract class BaseBottomDialogFragment extends BaseDialogFragment {

    private int mDialogHeight = 0;

    private int mThemeRes = R.style.FrameworkBottomDialogStyle;

    /**
     * return fragment content view id
     * */
    @LayoutRes
    protected abstract int configureContentViewRes();

    /**
     * 在这里配置Dialog的各项属性和自定义的ContentView
     * */
    protected abstract void configureDialog(Dialog dialog, View contentView);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog mDialog = new Dialog(getContext(), mThemeRes);
        View contentView = getActivity().getLayoutInflater().inflate(configureContentViewRes(), null);
        mDialog.setContentView(contentView);
        configureDialog(mDialog, contentView);
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        if (win == null) {
            return;
        }
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); //可设置dialog的位置
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
    
        WindowManager.LayoutParams params = win.getAttributes();
        if (mDialogHeight > 0) {
            win.setLayout(dm.widthPixels, mDialogHeight);
        } else {
            win.setLayout(dm.widthPixels, params.height);
        }
    }

    /**
     * 设置底部Dialog高度
     * */
    public void setDialogHeight(int height) {
        mDialogHeight = height;
    }

    /**
     * 如果不需要使用框架默认样式，可以在这里自定义样式
     * */
    public void setCustomStyle(@StyleRes int styleRes) {
        mThemeRes = styleRes;
    }

}
