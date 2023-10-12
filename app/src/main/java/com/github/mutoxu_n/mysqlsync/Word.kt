package com.github.mutoxu_n.mysqlsync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Entities")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "jp") val jp: String,
    @ColumnInfo(name = "en") val en: String
) {
    fun toWordMod(type: String) = WordMod(0, id, jp, en, type)
    fun changeId(newId: Long) = Word(newId, jp, en)
}
