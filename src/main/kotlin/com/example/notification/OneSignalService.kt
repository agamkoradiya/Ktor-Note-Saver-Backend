package com.example.notification

import com.example.notification.model.Notification


interface OneSignalService {

    suspend fun sendNotification(notification: Notification): Boolean

    companion object {
        // visit 'https://app.onesignal.com/' to generate new ONESIGNAL_APP_ID
        const val ONESIGNAL_APP_ID = "3ac716ec-c2f2-4a92-95fd-5efeb606995e"

        const val NOTIFICATIONS = "https://onesignal.com/api/v1/notifications"
    }
}