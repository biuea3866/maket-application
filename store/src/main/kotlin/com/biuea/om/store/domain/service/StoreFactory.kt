package com.biuea.om.store.domain.service

import com.biuea.om.store.domain.entity.IntegrationPlatform

class StoreFactory(
    private val registerStore: List<RegisterStore>,
    private val cancelStores: List<CancelStore>,
    private val registerCatalog: List<RegisterCatalog>
) {
    fun registerStore(
        platform: IntegrationPlatform,
        request: RegisterStoreRequest
    ) {
        this.registerStore.find { it.platform == platform }
            ?.register(request)
    }

    fun registerStoresBy(platforms: Map<IntegrationPlatform, RegisterStoreRequest>) {
        this.registerStore.forEach {
            if (it.platform in platforms.keys) it.register(platforms[it.platform]!!)
        }
    }

    fun registerAll(platforms: Map<IntegrationPlatform, RegisterStoreRequest>) {
        this.registerStore.forEach { it.register(platforms[it.platform]!!) }
    }
}

interface RegisterStoreRequest

interface CancelStoreRequest

interface RegisterCatalogRequest

interface CancelCatalogRequest

interface RegisterStore {
    val platform: IntegrationPlatform

    fun register(request: RegisterStoreRequest)
}

interface CancelStore {
    val platform: IntegrationPlatform

    fun cancel(request: CancelStoreRequest)
}

interface RegisterCatalog {
    fun register()
}

interface CancelCatalog {
    fun cancel()
}