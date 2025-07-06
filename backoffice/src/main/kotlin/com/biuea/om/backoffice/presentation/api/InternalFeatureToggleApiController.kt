package com.biuea.om.backoffice.presentation.api

import com.biuea.om.backoffice.application.FeatureToggleService
import com.biuea.om.backoffice.common.ApiResponse
import com.biuea.om.backoffice.domain.feature.FeatureToggle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.Long

@RestController
@RequestMapping("/backoffice")
class InternalFeatureToggleApiController(
    private val featureToggleService: FeatureToggleService
) {
    @GetMapping(value = ["/features"])
    fun getFeatures(): ApiResponse<List<GetFeatureResponse>> {
        return ApiResponse.success(featureToggleService.getAll())
    }
}

data class GetFeatureResponse(
    val id: Long,
    val name: String,
    val toggle: Boolean
) {
    companion object {
        fun of(featureToggle: FeatureToggle): GetFeatureResponse {
            return GetFeatureResponse(
                id = featureToggle.id,
                name = featureToggle.name,
                toggle = featureToggle.toggle,
            )
        }
    }
}