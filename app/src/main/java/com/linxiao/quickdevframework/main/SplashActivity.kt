package com.linxiao.quickdevframework.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.linxiao.framework.architecture.BaseSplashActivity
import com.linxiao.quickdevframework.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseSplashActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        keepSplashScreenUntilInitFinished()
        execInit()
    }

    private fun execInit() {
        Handler().postDelayed({
            keepSplashScreen.set(false)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}
