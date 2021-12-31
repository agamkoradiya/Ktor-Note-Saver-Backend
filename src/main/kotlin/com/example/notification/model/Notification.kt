package com.example.notification.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("include_external_user_ids")
    val includeExternalUserIds: List<String>,
    @SerialName("channel_for_external_user_ids")
    val channelForExternalUserIds: String,
    val headings: NotificationMessage,
    val contents: NotificationMessage,
    @SerialName("app_id")
    val appId: String,
)