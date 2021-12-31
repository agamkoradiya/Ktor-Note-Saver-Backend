package com.example.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class LogInResponse(
    val successful: Boolean,
    val message: String,
    val token: String?
)
