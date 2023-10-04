package com.github.mutoxu_n.mysqlsync

import android.app.Application

class App: Application() {
    companion object {
        private var _instance: App? = null
        val instance: App?
            get() { return _instance }


    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }
}