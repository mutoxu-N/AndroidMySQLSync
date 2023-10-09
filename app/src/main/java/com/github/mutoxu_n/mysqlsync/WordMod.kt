package com.github.mutoxu_n.mysqlsync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WordMods")
data class WordMod(
    @PrimaryKey(autoGenerate = true) val modId: Long,
    @ColumnInfo val id: Long,
    @ColumnInfo(name = "jp") val jp: String,
    @ColumnInfo(name = "en") val en: String,
    @ColumnInfo(name = "type") val type: String
) {
    companion object {
        const val TYPE_ADD = "ADD"
        const val TYPE_UPDATE = "UPDATE"
        const val TYPE_DELETE = "DELETE"
    }

    fun toWord(): Word = Word(id, jp, en)
}

