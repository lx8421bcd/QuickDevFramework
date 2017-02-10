package com.linxiao.framework.support.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.WindowManager;

import com.linxiao.framework.dialog.BaseDialogFragment;

/**
 * 提示对话框
 * Created by LinXiao on 2016-08-07.
 */
public class AlertDialogFragment extends DialogFragment {
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";

    private Drawable iconDrawable;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;
    private DialogInterface.OnDismissListener dismissListener;
    private DialogInterface.OnKeyListener keyListener;
    private String positiveBtnText;
    private String negativeBtnText;
    private boolean cancelable = true;

    public static AlertDialogFragment newInstance() {

        Bundle args = new Bundle();
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AlertDialogFragment setTitle(String title) {
        this.getArguments().putString(KEY_TITLE, title);
        return this;
    }

    public AlertDialogFragment setIcon(Drawable icon) {
        iconDrawable = icon;
        return this;
    }

    public AlertDialogFragment setMessage(String message) {
        this.getArguments().putString(KEY_MESSAGE, message);
        return this;
    }

    public AlertDialogFragment setPositiveButton(String text, DialogInterface.OnClickListener listener) {
        this.positiveListener = listener;
        this.positiveBtnText = text;
        return this;
    }

    public AlertDialogFragment setNegativeButton(String text, DialogInterface.OnClickListener listener) {
        this.negativeListener = listener;
        this.negativeBtnText = text;
        return this;
    }

    public AlertDialogFragment setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    public AlertDialogFragment setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public AlertDialogFragment setOnKeyListener(DialogInterface.OnKeyListener keyListener) {
        this.keyListener = keyListener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (iconDrawable != null) {
            builder.setIcon(iconDrawable);
        }
        builder.setMessage(message);
        if (positiveListener != null) {
            builder.setPositiveButton(positiveBtnText, positiveListener);
        }
        if (negativeListener != null) {
            builder.setNegativeButton(negativeBtnText, negativeListener);
        }
        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener);
        }
        if (keyListener != null) {
            builder.setOnKeyListener(keyListener);
        }
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
//        super.show(manager, tag);
    }

}
