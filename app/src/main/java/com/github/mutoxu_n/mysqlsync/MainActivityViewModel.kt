package com.github.mutoxu_n.mysqlsync

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel: ViewModel() {
    private var _words: MutableLiveData<List<Word>> = MutableLiveData(null)
    val words: LiveData<List<Word>>
        get() = _words

    fun getWordsFromRemote() {
        // TODO: FastAPIから受信する 
    }

    fun updateWords() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var new: List<Word> = listOf()
                new = RoomAccess.getAll()
                withContext(Dispatchers.Main) {
                    _words.value = new
                }
            }
        }
    }
}