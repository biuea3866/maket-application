package com.biuea.om.backoffice.presentation.api

import com.biuea.om.backoffice.application.FeatureToggleService
import com.biuea.om.backoffice.common.ApiResponse
import com.biuea.om.backoffice.domain.feature.FeatureToggle
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/backoffice")
class FeatureToggleApiController(
    private val featureToggleService: FeatureToggleService
) {
    @PostMapping(
        value = ["/features/register"],
        produces = ["application/json"]
    )
    fun registerFeature(@RequestBody body: RegisterFeature): ApiResponse<Boolean> {
        featureToggleService.registerFeature(
            name = body.name,
            toggle = body.toggle
        )

        return ApiResponse.success(true)
    }
}

data class RegisterFeature(
    val name: String,
    val toggle: Boolean
)