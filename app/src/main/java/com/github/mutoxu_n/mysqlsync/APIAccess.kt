package com.github.mutoxu_n.mysqlsync

import android.net.http.HttpException
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class APIAccess {
    companion object {
        private var address: String = "127.0.0.1"
        private var port: Int = 80
        private const val API_FAILED: String = "APIサーバーへのアクセスに失敗しました"
        private const val CONNECT_TIMEOUT = 3_000
        private const val READ_TIMEOUT = 3_000

        private fun reloadPreference() {
            address = App.pref.getString(App.KEY_ADDRESS, "127.0.0.1")!!
            port = App.pref.getInt(App.KEY_PORT, 80)
        }

        fun getVersion(): Long {
            try {
                reloadPreference()
                val url = URL("http://$address:$port/get/version")
                val con = url.openConnection() as HttpURLConnection
                con.connectTimeout = CONNECT_TIMEOUT
                con.readTimeout = READ_TIMEOUT
                con.requestMethod = "GET"
                con.connect()

                // 接続先で処理に以上があったら終了
                if(con.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("APIAccess.kt modify()", "レスポンスコード: ${con.responseCode}")
                    return -1L
                }
                val str = con.inputStream.bufferedReader(Charsets.UTF_8).use {br ->
                    br.readLines().joinToString("")
                }
                con.disconnect()

                try {
                    val json = JSONObject(str)
                    return json.getLong("version")

                } catch (e: Exception) { e.printStackTrace() }

            } catch (e: Exception) {
                Log.e("APIAccess.kt", API_FAILED)
                e.printStackTrace()
            }
            return -1L
        }

        fun getAll() {
            // API から Wordデータを取得してRoomに反映する
            try {
                reloadPreference()
                val url = URL("http://$address:$port/all")
                val con = url.openConnection() as HttpURLConnection
                con.connectTimeout = CONNECT_TIMEOUT
                con.readTimeout = READ_TIMEOUT
                con.requestMethod = "GET"
                con.connect()

                // 接続先で処理に以上があったら終了
                if(con.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("APIAccess.kt modify()", "レスポンスコード: ${con.responseCode}")
                    return
                }
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

                    // version を更新
                    val editor = App.pref.edit()
                    editor.putLong(App.KEY_VERSION, json.getLong("version"))
                    editor.apply()

                    RoomAccess.syncFromAPI(wordList)
                    con.disconnect()

                } catch (e: Exception) { e.printStackTrace() }

            } catch (e: Exception) {
                Log.e("APIAccess.kt", API_FAILED)
                e.printStackTrace()
            }
        }

        fun modify() {
            val wordMods = RoomAccess.getModifies()
            val array = JSONArray()
            for(wordMod in wordMods) {
                val elem = JSONObject()
                elem.put("modId", wordMod.modId)
                elem.put("id", wordMod.id)
                elem.put("jp", wordMod.jp)
                elem.put("en", wordMod.en)
                elem.put("type", wordMod.type)
                array.put(elem)
            }
            Log.i("APIAccess.kt", "modify json string: $array")
            val body = array.toString().toByteArray()

            reloadPreference()
            val url = URL("http://$address:$port/modify/")
            val con = url.openConnection() as HttpURLConnection
            con.connectTimeout = CONNECT_TIMEOUT
            con.readTimeout = READ_TIMEOUT
            con.requestMethod = "POST"

            try {
                con.setChunkedStreamingMode(0)
                con.setRequestProperty("Content-Type", "application/json")

                val oStream = con.outputStream
                oStream.write(body)
                oStream.flush()
                oStream.close()

                // 接続先で処理に以上があったら終了
                if(con.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("APIAccess.kt", API_FAILED)
                    return
                }

                // WordMod を全削除
                RoomAccess.deleteWordMods()

                // MySQLから取得
                getAll()

                con.disconnect()


            } catch (e: Exception) {
                Log.e("APIAccess.kt", "APIサーバーへのアクセスに失敗しました")
                e.printStackTrace()
            }

        }
    }
}