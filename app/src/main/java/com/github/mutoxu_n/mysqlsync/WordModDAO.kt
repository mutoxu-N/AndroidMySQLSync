package com.github.mutoxu_n.mysqlsync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WordModDAO {
    @Insert
    fun insert(wordMod: WordMod): Long

    @Update
    fun update(wordMod: WordMod)

    @Query("SELECT * FROM WordMods WHERE id=:idx")
    fun get(idx: Long): WordMod?

    @Query("SELECT * FROM WordMods")
    fun getAll(): List<WordMod>

    @Query("DELETE FROM WordMods WHERE id=:idx")
    fun deleteId(idx: Long)

    @Query("DELETE FROM WordMods")
    fun deleteAll()

    @Query("SELECT count(*) FROM WordMods")
    fun size(): Long
}