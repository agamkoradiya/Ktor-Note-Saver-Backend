package com.example.database

import com.example.data.model.Note
import com.example.data.model.User
import kotlinx.coroutines.flow.collect
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import org.mindrot.jbcrypt.BCrypt

class NotesDBImpl(private val database: CoroutineDatabase) : NotesDB {

    private val users = database.getCollection<User>()
    private val notes = database.getCollection<Note>()

    override suspend fun checkIfUserExists(email: String): Boolean {
        return users.findOne(User::email eq email) != null
    }

    override suspend fun registerUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun checkPasswordForEmail(email: String, password: String): Boolean {
        val hashedPassword = users.findOne(User::email eq email)?.password ?: return false
        return BCrypt.checkpw(password, hashedPassword)
    }

    override suspend fun getNotesForUser(email: String): List<Note> {
        val list = mutableListOf<Note>()
        notes.find(Note::owners contains email).toFlow().collect {
            list.add(it)
        }
        return list
    }

    override suspend fun saveNote(note: Note): Boolean {
        val isNoteExists = notes.findOneById(note.id) != null
        return if (isNoteExists) {
            notes.updateOneById(note.id, note).wasAcknowledged()
        } else {
            notes.insertOne(note).wasAcknowledged()
        }
    }

    override suspend fun deleteNoteForUser(email: String, noteID: String): Boolean {
        val note = notes.findOne(Note::id eq noteID, Note::owners contains email)
        note?.let { note ->
            if(note.owners.size > 1) {
                // the note has multiple owners, so we just delete the email from the owners list
                val newOwners = note.owners - email
                val updateResult = notes.updateOne(Note::id eq note.id, setValue(Note::owners, newOwners))
                return updateResult.wasAcknowledged()
            }
            return notes.deleteOneById(note.id).wasAcknowledged()
        } ?: return false
    }


    override suspend fun isOwnerOfNote(noteID: String, owner: String): Boolean {
        val note = notes.findOneById(noteID) ?: return false
        return owner in note.owners
    }


    override suspend fun addOwnerToNote(noteID: String, owner: String): Boolean {
        val owners = notes.findOneById(noteID)?.owners ?: return false
        return notes.updateOneById(noteID, setValue(Note::owners, owners + owner)).wasAcknowledged()
    }
}