package com.example.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse(
    val isSuccessful: Boolean,
    val message: String
)