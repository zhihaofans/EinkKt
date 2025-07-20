package com.zhihaofans.einkkt

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.zhihao.library.android.ZLibrary


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
        ZLibrary.init(applicationContext)
    }
}