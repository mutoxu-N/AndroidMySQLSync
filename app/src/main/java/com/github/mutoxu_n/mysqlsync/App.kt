package com.github.mutoxu_n.mysqlsync

import android.app.Application
import android.content.Context
import android.content.SharedPreferences


class App: Application() {
    companion object {
        const val KEY_ADDRESS = "address"
        const val KEY_PORT = "port"

        private lateinit var _instance: App
        val applicationContext: Context
            get() = _instance.applicationContext

        val pref: SharedPreferences
            get() = _instance.getSharedPreferences("config", Context.MODE_PRIVATE)

        fun convertDp2Px(dp: Float): Float {
            val metrics = applicationContext.resources.displayMetrics
            return dp * metrics.density
        }

    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }
}