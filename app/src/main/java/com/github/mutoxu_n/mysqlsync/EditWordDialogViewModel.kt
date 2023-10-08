package com.github.mutoxu_n.mysqlsync

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditWordDialogViewModel: ViewModel() {
    private val _id: MutableLiveData<Long> = MutableLiveData(0)
    val id: LiveData<Long> get() = _id

    private val _jp: MutableLiveData<String> = MutableLiveData("")
    val jp: LiveData<String> get() = _jp

    private val _en: MutableLiveData<String> = MutableLiveData("")
    val en: LiveData<String> get() = _en


    fun setId(id: Long) {
        _id.value = id
        if(id != 0L) {
            // Roomからデータ取得
            viewModelScope.launch { withContext(Dispatchers.IO) {
                val dao = Database.getDatabase().wordDAO()
                val word = dao.get(id)
                withContext(Dispatchers.Main) {
                    if(word == null) {
                        _id.value = 0L
                    } else {
                        _jp.value = word.jp
                        _en.value = word.en
                    }
                }
            } }
        }
    }
    fun setJp(jp: String) { _jp.value = jp  }
    fun setEn(en: String) { _en.value = en }

    fun save() {
        val word = Word(id.value!!, jp.value!!, en.value!!)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dao = Database.getDatabase().wordDAO()
                if(word.id == 0L) dao.insert(word)
                else dao.update(word)
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dao = Database.getDatabase().wordDAO()
                dao.deleteId(id.value!!)
            }
        }
    }

}