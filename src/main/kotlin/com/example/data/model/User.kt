package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    val email: String,
    val password: String,
    @BsonId
    val id: String = ObjectId().toString()
)