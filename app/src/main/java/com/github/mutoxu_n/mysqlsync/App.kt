package com.github.mutoxu_n.mysqlsync

import android.app.Application
import android.content.Context

class App: Application() {
    companion object {
        private lateinit var _instance: App
        val applicationContext: Context
            get() { return _instance.applicationContext }


    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }
}