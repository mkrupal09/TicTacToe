package com.example.myapplication

import android.app.Application
import com.bugfender.sdk.Bugfender

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Bugfender.init(this, "lUQOfcVj4Gz48YBtFF7OEioJUqwyL0zZ", BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
        Bugfender.enableUIEventLogging(this)
        Bugfender.enableLogcatLogging() // optional, if you want logs automatically collected from logcat
    }
}