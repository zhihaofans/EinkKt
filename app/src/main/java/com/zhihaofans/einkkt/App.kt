package com.zhihaofans.einkkt

import android.app.Application
import io.zhihao.library.android.ZLibrary

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        ZLibrary.init(applicationContext)
    }
}