package com.biuea.om.store.domain.service

import com.biuea.om.store.domain.entity.Store
import com.biuea.om.store.infrastructure.external.notification.NotificationSender
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
open class StoreEventProcessor(
    private val notificationSender: List<NotificationSender>,
    private val productService: ProductService
) {
    @Async
    @EventListener
    open fun onRegisterStore(payload: OnRegisterStore) {
        notificationSender.forEach { it.send() }
    }

    @Async
    @EventListener
    open fun onCancelStore(payload: OnCancelStore) {
        notificationSender.forEach { it.send() }
        productService.clearProducts()
    }
}

data class OnRegisterStore(val store: Store)

data class OnCancelStore(val store: Store)