package com.github.mutoxu_n.mysqlsync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private var _words: MutableLiveData<List<Word>> = MutableLiveData(null)
    val words: LiveData<List<Word>>
        get() = _words

    fun getWordsFromRemote() {
        // TODO: FastAPIから受信する 
    }
}