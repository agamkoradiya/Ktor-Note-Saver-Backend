package com.example.database

import com.example.data.model.Note
import com.example.data.model.User

interface NotesDB {

    suspend fun checkIfUserExists(email: String): Boolean
    suspend fun registerUser(user: User): Boolean
    suspend fun checkPasswordForEmail(email: String, password: String): Boolean
    suspend fun getNotesForUser(email: String): List<Note>
    suspend fun saveNote(note: Note): Boolean
    suspend fun deleteNoteForUser(email: String, noteID: String): Boolean
    suspend fun isOwnerOfNote(noteID: String, owner: String): Boolean
    suspend fun addOwnerToNote(noteID: String, owner: String): Boolean
}