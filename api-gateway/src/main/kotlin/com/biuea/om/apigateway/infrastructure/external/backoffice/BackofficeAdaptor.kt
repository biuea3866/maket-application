package com.biuea.om.apigateway.infrastructure.external.backoffice

import com.biuea.om.apigateway.common.FeatureToggle
import org.springframework.stereotype.Component

@Component
class BackofficeAdaptor(
    private val backofficeClient: BackofficeClient
) {
    fun getAllFeatures(): List<FeatureToggle> {
        return this.backofficeClient.getFeatures().data?.map {
            FeatureToggle(
                name = it.name,
                toggle = it.toggle
            )
        }?: emptyList()
    }
}