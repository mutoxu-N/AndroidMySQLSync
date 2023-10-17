package com.github.mutoxu_n.mysqlsync

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class APIAccess {
    companion object {
        private lateinit var url: URL
        private var port: Int = 80
        private lateinit var con: HttpURLConnection
        private fun createConnection(method: String = "GET") {
            // コネクション確立
            con = url.openConnection() as HttpURLConnection
            con.connectTimeout = 3_000
            con.readTimeout = 3_000
            con.requestMethod = method
            con.connect()
        }

        private fun createURL(path: String): URL {
            // ポート更新
            port = App.pref.getInt(App.KEY_PORT, 80)
            return URL("http://${App.pref.getString(App.KEY_ADDRESS, "127.0.0.1")}:$port$path")
        }

        fun getVersion(): Long {
            try {
                url = createURL("/get/version")
                createConnection()

                val str = con.inputStream.bufferedReader(Charsets.UTF_8).use {br ->
                    br.readLines().joinToString("")
                }

                try {
                    val json = JSONObject(str)
                    val ver = json.getLong("version")
                    return ver
                } catch (e: Exception) { e.printStackTrace() }

            } catch (e: Exception) {
                Log.e("APIAccess.kt", "APIサーバーへのアクセスに失敗しました")
                e.printStackTrace()
            }
            return -1L
        }

        fun getAll() {
            // API から Wordデータを取得してRoomに反映する
            try {
                url = createURL("/all")
                createConnection()

                val str = con.inputStream.bufferedReader(Charsets.UTF_8).use {br ->
                    br.readLines().joinToString("")
                }

                try {
                    val wordList = mutableListOf<Word>()
                    val json = JSONObject(str)
                    val data = json.getJSONArray("data")
                    for(i in 0 until data.length()) {
                        val wordJSON = data.getJSONObject(i)
                        val word = Word(
                            wordJSON.getLong("ID"),
                            wordJSON.getString("jp"),
                            wordJSON.getString("en")
                        )
                        wordList.add(word)
                    }

                    RoomAccess.syncMySQL(wordList)
                    con.disconnect()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                Log.e("APIAccess.kt", "APIサーバーへのアクセスに失敗しました")
                e.printStackTrace()
            }
        }
    }
}