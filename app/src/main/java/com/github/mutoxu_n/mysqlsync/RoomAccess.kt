package com.github.mutoxu_n.mysqlsync

class RoomAccess {
    companion object {
        fun insert(word: Word) {
            val wordDAO = Database.getDatabase().wordDAO()
            val wordModDAO = Database.getDatabase().wordModDAO()
            val inserted = wordDAO.insert(word)
            word.changeId(inserted)
            wordModDAO.insert(WordMod.fromWord(word, WordMod.TYPE_INSERT))
        }

        fun update(word: Word) {
            val wordDAO = Database.getDatabase().wordDAO()
            val wordModDAO = Database.getDatabase().wordModDAO()
            wordDAO.update(word)
            wordModDAO.insert(WordMod.fromWord(word, WordMod.TYPE_UPDATE))
        }

        fun get(id: Long): Word? {
            val wordDAO = Database.getDatabase().wordDAO()
            return wordDAO.get(id)
        }

        fun getAll(): List<Word> {
            val wordDAO = Database.getDatabase().wordDAO()
            return wordDAO.getAll()
        }

        private fun delete(word: Word) {
            val wordDAO = Database.getDatabase().wordDAO()
            val wordModDAO = Database.getDatabase().wordModDAO()
            wordDAO.deleteId(word.id)
            wordModDAO.insert(WordMod.fromWord(word, WordMod.TYPE_DELETE))
        }

        fun deleteId(id: Long) {
            val wordDAO = Database.getDatabase().wordDAO()
            val word = wordDAO.get(id)
            word?.let { delete(it) }
        }
    }
}