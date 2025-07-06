package com.biuea.om.store.presentation.api

import com.biuea.om.store.application.StoreFacade
import com.biuea.om.store.common.ApiResponse
import com.biuea.om.store.infrastructure.cache.FeatureToggleManager
import org.springframework.cache.CacheManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/stores"])
class StoreApiController(
    private val storeFacade: StoreFacade,
    private val featureToggleManager: FeatureToggleManager,
    private val cacheManager: CacheManager
) {
    @PostMapping(value = ["/register"])
    fun registerStore(
        @RequestHeader("X-Api-Version") version: String,
        @RequestHeader("X-User-Id") userId: Long,
        @RequestBody body: RegisterStoreRequest
    ): ApiResponse<Unit> {
        storeFacade.registerStore(
            businessName = body.businessName,
            businessNumber = body.businessNumber,
            representativeName = body.representativeName,
            email = body.email,
            phone = body.phone,
            userId = userId,
            description = body.description,
            platform = body.platform,
        )

        return ApiResponse.success(null)
    }
}