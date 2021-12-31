package com.example.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteNoteRequest(
    val noteId: String
)