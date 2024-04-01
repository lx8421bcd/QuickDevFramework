package com.linxiao.quickdevframework.sample.frameworkapi

import android.app.IntentService
import android.content.Intent
import com.linxiao.framework.dialog.showAlert

/**
 * 用于测试全局ActivityDialog的使用
 *
 * @author lx8421bcd
 * @since 2016-12-11
 */
class BackgroundService : IntentService("BackgroundService") {
    override fun onHandleIntent(intent: Intent?) {
        showAlert("this is top dialog from service")
    }
}
