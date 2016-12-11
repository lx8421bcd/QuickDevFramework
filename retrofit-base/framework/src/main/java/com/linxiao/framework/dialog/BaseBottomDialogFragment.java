package com.linxiao.framework.dialog;

import android.app.Dialog;
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
import com.linxiao.framework.util.DensityUtil;

/**
 * 从底部显示的全屏Dialog基类
 * Created by LinXiao on 2016-12-12.
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
        Window win = mDialog.getWindow();
        if (win != null) {
            win.getDecorView().setPadding(0, 0, 0, 0);
            win.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); //可设置dialog的位置
        }
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window win = dialog.getWindow();
        if (win == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int[] screenSize = DensityUtil.getScreenSize(getActivity());
        WindowManager.LayoutParams params =  win.getAttributes();
        if (mDialogHeight > 0) {
            win.setLayout(screenSize[0], mDialogHeight);
        }
        else {
            win.setLayout(screenSize[0], params.height);
        }
    }

    public void setDialogHeight(int height) {
        mDialogHeight = height;
    }

    public void setCustomStyle(@StyleRes int styleRes) {
        mThemeRes = styleRes;
    }

}
