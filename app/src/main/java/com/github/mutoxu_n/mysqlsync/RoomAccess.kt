package com.github.mutoxu_n.mysqlsync

class RoomAccess {
    companion object {
        private fun wordModDAOInsert(wordMod: WordMod) {
            val wordModDAO = Database.getDatabase().wordModDAO()
            val n = wordModDAO.size()
            if(n == 0L) {
                // WordModが存在するならRoomID+1
                val editor = App.pref.edit()
                editor.putLong(App.KEY_VERSION, App.pref.getLong(App.KEY_VERSION, 0L)+1)
                editor.apply()
            }
            wordModDAO.insert(wordMod)
        }

        fun insert(word: Word) {
            val wordDAO = Database.getDatabase().wordDAO()
            val inserted = wordDAO.insert(word)
            wordModDAOInsert(WordMod.fromWord(word.changeId(inserted), WordMod.TYPE_INSERT))
        }

        fun update(word: Word) {
            val wordDAO = Database.getDatabase().wordDAO()
            wordDAO.update(word)
            wordModDAOInsert(WordMod.fromWord(word, WordMod.TYPE_UPDATE))
        }

        fun get(id: Long): Word? {
            val wordDAO = Database.getDatabase().wordDAO()
            return wordDAO.get(id)
        }

        fun getAll(): List<Word> {
            val wordDAO = Database.getDatabase().wordDAO()
            return wordDAO.getAll()
        }

        fun getModifies(): List<WordMod> {
            val wordModDAO = Database.getDatabase().wordModDAO()
            return wordModDAO.getAll()
        }

        fun getModifiesSize(): Long {
            val wordModDAO = Database.getDatabase().wordModDAO()
            return wordModDAO.size()
        }

        private fun delete(word: Word) {
            val wordDAO = Database.getDatabase().wordDAO()
            wordDAO.deleteId(word.id)
            wordModDAOInsert(WordMod.fromWord(word, WordMod.TYPE_DELETE))
        }

        fun deleteId(id: Long) {
            val wordDAO = Database.getDatabase().wordDAO()
            val word = wordDAO.get(id)
            word?.let { delete(it) }
        }

        fun deleteWordMods() {
            val wordModDAO = Database.getDatabase().wordModDAO()
            wordModDAO.deleteAll()
        }

        fun syncFromAPI(words: List<Word>) {
            val wordDAO = Database.getDatabase().wordDAO()
            wordDAO.deleteAll()
            wordDAO.insertAll(words)
        }
    }
}