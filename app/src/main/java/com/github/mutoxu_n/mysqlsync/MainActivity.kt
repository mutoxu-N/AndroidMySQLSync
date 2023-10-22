package com.github.mutoxu_n.mysqlsync

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mutoxu_n.mysqlsync.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), EditWordDialogFragment.EditDialogInterface {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)

        // buttons
        binding.add.setOnClickListener {
            val dialog = EditWordDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "AddWordDialog")
        }
        binding.update.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            APIAccess.modify()
                            APIAccess.getAll()
                            withContext(Dispatchers.Main) { viewModel.updateWords() }

                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
        }
        binding.config.setOnClickListener {
            val dialog = RemoteConfigureDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "RemoteConfigureDialog")
        }

        // recycler view settings
        val layout = LinearLayoutManager(this@MainActivity)
        binding.list.layoutManager = layout
        binding.list.addItemDecoration(DividerItemDecoration(this@MainActivity, layout.orientation))
        viewModel.words.observe(this) {it?.let { // words changed
            binding.list.adapter = ViewAdapter(it)
        } }
        viewModel.updateWords()

        setContentView(binding.root)

        // 同期
        lifecycleScope.launch {
             repeatOnLifecycle(Lifecycle.State.RESUMED) {
                 var cnt = 0L
                 withContext(Dispatchers.IO) {
                     while(true) {
                         // SYNC_INTERVALごとに行う処理
                         if(cnt >= SYNC_INTERVAL) {
                             // 同期の実行

                             //アクセス中は表示を0秒に変更
                             cnt = 0L
                             withContext(Dispatchers.Main) {
                                 binding.status.text = getString(
                                     R.string.status_text,
                                     if(viewModel.isOnline) "ONLINE" else "OFFLINE",
                                     0L
                                 )
                             }

                             // バージョン比較
                             val roomVersion = App.pref.getLong(App.KEY_VERSION, 0L)
                             val mySQLVersion = APIAccess.getVersion()
                             withContext(Dispatchers.Main) { Toast.makeText(this@MainActivity, "room: $roomVersion, MySQL: $mySQLVersion", Toast.LENGTH_SHORT).show() }
                             if(mySQLVersion == -1L) {
                                 withContext(Dispatchers.Main) {
                                     viewModel.setIsOnline(false)
                                 }

                             } else if(roomVersion == mySQLVersion) {
                                 // 変更が必要ない場合
                                 withContext(Dispatchers.Main) {
                                     viewModel.setIsOnline(true)
                                 }

                             } else if(RoomAccess.getModifiesSize() > 0L) {
                                 // 変更が有ったらサーバーに送信
                                 if(APIAccess.modify()) {
                                     // if synced
                                     withContext(Dispatchers.Main) {
                                         viewModel.setIsOnline(true)
                                         viewModel.updateWords()
                                     }

                                 } else {
                                     // if sync failed
                                     withContext(Dispatchers.Main) {
                                         viewModel.setIsOnline(false)
                                     }
                                 }
                             }
                         }

                         // INTERVALごとに行う処理
                         withContext(Dispatchers.Main) {
                             // update status text
                             val statusText = binding.status
                             statusText.text = getString(
                                 R.string.status_text,
                                 if(viewModel.isOnline) "ONLINE" else "OFFLINE",
                                 (SYNC_INTERVAL-cnt)/1000
                             )
                             statusText.setBackgroundColor(Color.parseColor(if(viewModel.isOnline) "#adffb0" else "#ffadaf"))

                         }

                         cnt += INTERVAL
                         delay(INTERVAL)
                     }
                 }
             }
        }
    }


    private inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var parent: View
        var jp: TextView
        var en: TextView
        init {
            parent = view
            jp = view.findViewById(android.R.id.text1)
            en = view.findViewById(android.R.id.text2)
        }
    }

    private inner class ViewAdapter(val words: List<Word>): RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(this@MainActivity)
            val view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val word = words[position]
            holder.jp.text = "${word.id}. ${word.jp}"
            holder.en.text = word.en
            holder.parent.setOnClickListener {
                val dialog = EditWordDialogFragment.newInstance(word.id)
                dialog.show(supportFragmentManager, "EditWordDialog (id=${word.id})")
            }
        }

        override fun getItemCount(): Int {
            return words.size
        }
    }

    override fun onWordEdited() {
        // 単語が編集されたらRecyclerViewを更新
        viewModel.updateWords()
    }

    companion object {
        private const val INTERVAL: Long = 1_000
        private const val SYNC_INTERVAL: Long = 30_000
    }
}