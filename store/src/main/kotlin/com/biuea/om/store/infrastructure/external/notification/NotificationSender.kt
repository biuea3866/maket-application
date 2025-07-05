package com.biuea.om.store.infrastructure.external.notification

interface NotificationSender {
    val type: NotificationType

    fun send()
}

enum class NotificationType {
    MAIL,
    SMS
}