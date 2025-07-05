package com.biuea.om.store.infrastructure.external.notification

import org.springframework.stereotype.Component

@Component
class MailSender: NotificationSender {
    override val type: NotificationType
        get() = NotificationType.MAIL

    override fun send() {
        println("Sending Email")
    }
}