package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Note(
    val title: String,
    val content: String,
    val date: Long,
    val owners: List<String>,
    @BsonId
    val id: String = ObjectId().toString()
)