package com.biuea.om.store.domain.service

import com.biuea.om.store.domain.entity.Store
import com.biuea.om.store.domain.value.StoreRegistrationInfo
import com.biuea.om.store.infrastructure.mysql.store.StoreJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreService(
    private val storeRepository: StoreJpaRepository
) {
    @Transactional
    fun registerStore(
        name: String,
        description: String?,
        userId: Long,
        registrationInfo: StoreRegistrationInfo
    ): Store {
        val store = Store.register(
            name = name,
            description = description,
            userId = userId,
            registrationInfo = registrationInfo,
        )
        store.addStoreIntegration(registrationInfo)
        return this.storeRepository.save(store)
    }
}