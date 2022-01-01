package com.learn.notesapp.repository

import com.learn.notesapp.db.DataBase
import com.learn.notesapp.model.Note

class NoteRepository(private val db : DataBase) {

    fun getNote() = db.getNoteDao().getAllNote()

    fun searchNote(query:String) = db.getNoteDao().searchNote(query)

    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)

}