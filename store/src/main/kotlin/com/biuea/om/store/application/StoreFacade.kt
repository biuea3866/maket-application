package com.biuea.om.store.application

import com.biuea.om.store.domain.entity.IntegrationPlatform
import com.biuea.om.store.domain.service.OnRegisterStore
import com.biuea.om.store.domain.service.StoreIntegrationFactory
import com.biuea.om.store.domain.service.StoreService
import com.biuea.om.store.domain.value.StoreRegistrationRequestInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class StoreFacade(
    private val storeIntegrationFactory: StoreIntegrationFactory,
    private val storeService: StoreService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun registerStore(
        businessName: String,
        businessNumber: String,
        representativeName: String,
        email: String,
        phone: String,
        userId: Long,
        description: String?,
        platform: IntegrationPlatform
    ) {
        val storeRegistration = this.storeIntegrationFactory.registerStore(
            platform = platform,
            request = StoreRegistrationRequestInfo(
                businessName = businessName,
                businessNumber = businessNumber,
                representativeName = representativeName,
                email = email,
                phone = phone,
            )
        )
        val store = storeService.registerStore(
            name = businessName,
            description = description,
            userId = userId,
            registrationInfo = storeRegistration
        )
        applicationEventPublisher.publishEvent(OnRegisterStore(store))
    }
}