package com.biuea.om.store.infrastructure.external.backoffice

import com.biuea.om.store.common.ApiResponse
import com.biuea.om.store.common.FeatureToggle
import com.biuea.om.store.infrastructure.external.config.FeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "backoffice-client",
    url = "http://localhost:20000/backoffice/",
    configuration = [FeignConfig::class]
)
interface BackofficeClient {
    @GetMapping(value = ["/features"])
    fun getFeatures(): ApiResponse<List<GetFeatureResponse>>
}

data class GetFeatureResponse(
    val id: Long,
    val name: String,
    val toggle: Boolean
)