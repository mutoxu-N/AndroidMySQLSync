package com.github.mutoxu_n.mysqlsync

class RoomAccess {
    companion object {
        fun insert(word: Word) {
            val wordDao = Database.getDatabase().wordDAO()
            wordDao.insert(word)
        }

        fun update(word: Word) {
            val wordDao = Database.getDatabase().wordDAO()
            wordDao.update(word)
        }

        fun get(id: Long): Word? {
            val wordDao = Database.getDatabase().wordDAO()
            return wordDao.get(id)
        }

        fun getAll(): List<Word> {
            val wordDao = Database.getDatabase().wordDAO()
            return wordDao.getAll()
        }

        fun deleteId(id: Long) {
            val wordDao = Database.getDatabase().wordDAO()
            wordDao.deleteId(id)
        }
    }
}