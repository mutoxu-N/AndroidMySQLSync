package com.github.mutoxu_n.mysqlsync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditWordDialogViewModel: ViewModel() {
    private val _id: MutableLiveData<Long> = MutableLiveData(null)
    val id: LiveData<Long> get() = _id

    private val _jp: MutableLiveData<String> = MutableLiveData(null)
    val jp: LiveData<String> get() = _jp

    private val _en: MutableLiveData<String> = MutableLiveData(null)
    val en: LiveData<String> get() = _en


    fun setId(id: Long) {
        _id.value = id
        if(id != 0L) {
            // TODO: Roomから取得
        }
    }
    fun setJp(jp: String) { _jp.value = jp  }
    fun setEn(en: String) { _en.value = en }

    fun save() {
        // TODO: Roomに保存 
    }

}