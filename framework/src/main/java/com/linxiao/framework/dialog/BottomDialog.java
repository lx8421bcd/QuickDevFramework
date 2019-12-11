package com.linxiao.framework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.linxiao.framework.R;


/**
 * 底部弹出Dialog基类
 *
 * @author linxiao
 * @since 2017-06-19
 */
public class BottomDialog extends Dialog {

    private int mDialogHeight = 0;

    public BottomDialog(@NonNull Context context) {
        this(context, R.style.FrameworkBottomDialogStyle);
    }

    BottomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window win = this.getWindow();
        if (win == null) {
            return;
        }
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); //可设置dialog的位置
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        WindowManager.LayoutParams params =  win.getAttributes();
        if (mDialogHeight > 0) {
            win.setLayout(dm.widthPixels, mDialogHeight);
        }
        else {
            win.setLayout(dm.widthPixels, params.height);
        }
    }

    /**
     * 设置底部Dialog高度
     * */
    public void setDialogHeight(int height) {
        mDialogHeight = height;
    }
}
