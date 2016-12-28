package com.linxiao.framework.support.dialog;

import android.support.annotation.NonNull;

import com.linxiao.framework.event.ShowAlertDialogEvent;

import org.greenrobot.eventbus.EventBus;

/**
 *
 * Created by LinXiao on 2016-12-12.
 */
public class AlertDialogOperator {
    private ShowAlertDialogEvent event;

    public AlertDialogOperator(@NonNull ShowAlertDialogEvent event) {
        this.event = event;
    }

    public void show() {
        EventBus.getDefault().post(event);
    }
}
