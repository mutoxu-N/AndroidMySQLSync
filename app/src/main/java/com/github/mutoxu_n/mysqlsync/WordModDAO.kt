package com.github.mutoxu_n.mysqlsync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WordModDAO {
    @Insert
    fun insert(wordMod: WordMod): Long

    @Query("SELECT * FROM WordMods")
    fun getAll(): List<WordMod>

    @Query("DELETE FROM WordMods")
    fun deleteAll()

    @Query("SELECT count(*) FROM WordMods")
    fun size(): Long
}