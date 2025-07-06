package com.biuea.om.store.common

import com.biuea.om.store.infrastructure.cache.FeatureToggleManager
import com.biuea.om.store.infrastructure.external.backoffice.BackofficeAdaptor
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

data class FeatureToggle(
    val name: String,
    val toggle: Boolean
) {
}

@Component
class FeatureToggleInitializer(
    private val backofficeAdaptor: BackofficeAdaptor,
    private val featureToggleManager: FeatureToggleManager
) {
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady(event: ApplicationReadyEvent) {
        val features = backofficeAdaptor.getAllFeatures()
        featureToggleManager.updateFeatureToggles(features.associateBy { it.name }.mapValues { it.value.toggle })
    }
}