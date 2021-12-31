package com.example.data.responses

import com.example.data.model.Note
import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse(
    val isSuccessful: Boolean,
    val notes: List<Note> = emptyList()
)
