package com.biuea.om.store.infrastructure.external.notification

import org.springframework.stereotype.Component

@Component
class SmsSender: NotificationSender {
    override val type: NotificationType
        get() = NotificationType.SMS

    override fun send() {
        println("Sending SMS")
    }
}