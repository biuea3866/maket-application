package com.biuea.om.store.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService {
    @Transactional
    fun clearProducts() {
        println("Clearing all products")
    }
}