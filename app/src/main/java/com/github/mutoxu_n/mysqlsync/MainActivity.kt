package com.github.mutoxu_n.mysqlsync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mutoxu_n.mysqlsync.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
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
        binding.update.setOnLongClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    APIAccess.modify()
                }
            }
            true
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
}