package com.biuea.om.store.infrastructure.external.backoffice

import com.biuea.om.store.common.FeatureToggle
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