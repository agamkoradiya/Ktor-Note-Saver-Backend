package com.example.data.requests

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class AccountRequest(
    val email: String,
    val password: String
){
    fun hashedPassword(): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}