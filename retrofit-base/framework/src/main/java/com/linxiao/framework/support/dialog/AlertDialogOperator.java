package com.linxiao.framework.support.dialog;

import android.support.annotation.NonNull;

import com.linxiao.framework.event.ShowActivityDialogEvent;

import org.greenrobot.eventbus.EventBus;

/**
 *
 * Created by LinXiao on 2016-12-12.
 */
public class AlertDialogOperator {
    private ShowActivityDialogEvent event;

    public AlertDialogOperator(@NonNull ShowActivityDialogEvent event) {
        this.event = event;
    }

    public void show() {
        EventBus.getDefault().post(event);
    }
}
