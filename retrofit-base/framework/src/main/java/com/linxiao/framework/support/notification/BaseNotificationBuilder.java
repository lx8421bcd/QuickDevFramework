package com.linxiao.framework.support.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

/**
 *
 * Created by linxiao on 2016/12/2.
 */
public abstract class BaseNotificationBuilder {
    protected static String TAG;
    
    protected Context mContext;
    protected NotificationCompat.Builder mBuilder;
    protected Intent destIntent;
    protected TaskStackBuilder stackBuilder;

    public BaseNotificationBuilder(Context context, @NonNull String title, @NonNull String message, @NonNull Intent destIntent) {
        TAG = this.getClass().getSimpleName();
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(NotificationWrapper.getDefaultIconRes())
                .setContentTitle(title)
                .setContentText(message);
        this.destIntent = destIntent;
        stackBuilder = TaskStackBuilder.create(context);
        mContext = context;
    }

    public BaseNotificationBuilder configureBuilder(BuilderConfigurator configurator) {
        configurator.configure(mBuilder);
        return this;
    }

    public BaseNotificationBuilder addToParentStack(Class<?> sourceActivityClass) {
        stackBuilder.addParentStack(sourceActivityClass);
        return this;
    }

    /**
     * 通过反射解析到通知事件响应的Activity Class对象，并加入返回栈
     * <p><strong>
     *  注意：目标Activity要想在back时成功回退到指定的Activity必须在该Activity的manifest声明中添加
     *      <br>android:parentActivityName="回退Activity路径"<br>
     *  的属性，否则将不会生效
     * </strong></p>
     * */
    public BaseNotificationBuilder addToParentStack() {
        String className = destIntent.getComponent().getClassName();
        try {
            Class<?> sourceActivityClass = Class.forName(className);
            stackBuilder.addParentStack(sourceActivityClass);
            Log.d(TAG, "sendSimpleNotification: sourceActivityClass = " + sourceActivityClass.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BaseNotificationBuilder setResumeAppIfBackground() {
        destIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return this;
    }

    public void send(int notifyId) {
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        stackBuilder.addNextIntent(destIntent);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, destIntent, 0);
        mBuilder.setContentIntent(pendingIntent);
        manager.notify(notifyId, mBuilder.build());
    }

    public void send() {
        send(new Random().nextInt(65536));
    }


}
