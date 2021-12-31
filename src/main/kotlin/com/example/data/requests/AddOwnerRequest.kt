package com.example.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AddOwnerRequest(
    val noteID: String,
    val owner: String,
    val wantToSendNotification: Boolean
)