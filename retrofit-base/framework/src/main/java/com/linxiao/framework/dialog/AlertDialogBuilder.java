package com.linxiao.framework.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;


/**
 *
 * Created by LinXiao on 2016-12-12.
 */
public class AlertDialogBuilder {

    private Context context;

    private String title;
    private String message;
    private Drawable icon;
    private String positiveText;
    private String negativeText;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;
    private boolean cancelable;

    public AlertDialogBuilder(Context context) {
        this.context = context;
    }

    public AlertDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertDialogBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public AlertDialogBuilder setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public AlertDialogBuilder setPositiveText(String positiveText) {
        this.positiveText = positiveText;
        return this;
    }

    public AlertDialogBuilder setNegativeText(String negativeText) {
        this.negativeText = negativeText;
        return this;
    }

    public AlertDialogBuilder setPositiveButton(DialogInterface.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
        return this;
    }

    public AlertDialogBuilder setNegativeButton(DialogInterface.OnClickListener negativeListener) {
        this.negativeListener = negativeListener;
        return this;
    }

    public AlertDialogBuilder setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public void show() {
        AlertTransmitter transmitter = new AlertTransmitter();
        transmitter.setMessage(message);
        transmitter.setTitle(title);
        transmitter.setIcon(icon);
        transmitter.setPositiveText(positiveText);
        transmitter.setNegativeText(negativeText);
        transmitter.setPositiveListener(positiveListener);
        transmitter.setNegativeListener(negativeListener);
        transmitter.setCancelable(cancelable);

        String transmitterId = String.valueOf(System.currentTimeMillis());
        TopDialogActivity.addAlertDataTransmitter(transmitterId, transmitter);
        Intent intent = new Intent(context, TopDialogActivity.class);
        intent.putExtra(TopDialogActivity.KEY_TRANSMITTER_ID, transmitterId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
