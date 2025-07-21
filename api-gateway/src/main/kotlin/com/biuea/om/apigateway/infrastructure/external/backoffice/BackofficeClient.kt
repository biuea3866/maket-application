package com.biuea.om.apigateway.infrastructure.external.backoffice

import com.biuea.om.apigateway.common.ApiResponse
import com.biuea.om.apigateway.config.FeignConfig
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