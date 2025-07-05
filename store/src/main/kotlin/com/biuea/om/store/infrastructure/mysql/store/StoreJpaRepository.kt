package com.biuea.om.store.infrastructure.mysql.store

import com.biuea.om.store.domain.entity.Store
import org.springframework.data.jpa.repository.JpaRepository

interface StoreJpaRepository: JpaRepository<Store, Long> {
    fun save(store: Store): Store
}