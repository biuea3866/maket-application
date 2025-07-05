package com.biuea.om.store.domain.service

import com.biuea.om.store.domain.entity.IntegrationPlatform
import com.biuea.om.store.domain.value.StoreRegistrationInfo
import com.biuea.om.store.domain.value.StoreRegistrationRequestInfo
import org.springframework.stereotype.Service

@Service
class StoreIntegrationFactory(
    private val registerStore: List<RegisterStore>,
    private val cancelStores: List<CancelStore>,
    private val registerProduct: List<RegisterProduct>
) {
    fun registerStore(
        platform: IntegrationPlatform,
        request: StoreRegistrationRequestInfo
    ): StoreRegistrationInfo {
        return this.registerStore.find { it.platform == platform }
            ?.register(request)
            ?: throw IllegalStateException("Can't find registerStore for platform: $platform")
    }

    fun registerStoresBy(platforms: Map<IntegrationPlatform, StoreRegistrationRequestInfo>) {
        this.registerStore.forEach {
            if (it.platform in platforms.keys) it.register(platforms[it.platform]!!)
        }
    }

    fun registerAll(platforms: Map<IntegrationPlatform, StoreRegistrationRequestInfo>) {
        this.registerStore.forEach { it.register(platforms[it.platform]!!) }
    }
}

interface CancelStoreRequest

interface RegisterCatalogRequest

interface CancelCatalogRequest

interface RegisterStore {
    val platform: IntegrationPlatform

    fun register(registrationInformation: StoreRegistrationRequestInfo): StoreRegistrationInfo
}

interface CancelStore {
    val platform: IntegrationPlatform

    fun cancel(request: CancelStoreRequest)
}

interface RegisterProduct {
    val platform: IntegrationPlatform

    fun register()
}

interface CancelProduct {
    val platform: IntegrationPlatform

    fun cancel()
}