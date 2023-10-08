package com.github.mutoxu_n.mysqlsync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mutoxu_n.mysqlsync.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
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
        binding.update.setOnClickListener { viewModel.updateWords() }
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
        var jp: TextView
        var en: TextView
        init {
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
            holder.jp.text = "${word.jp} (${word.id})"
            holder.en.text = word.en
        }

        override fun getItemCount(): Int {
            return words.size
        }
    }
}