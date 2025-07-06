package com.biuea.om.store.presentation.consumer

import com.biuea.om.store.infrastructure.cache.FeatureToggleManager
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class FeatureToggleUpdater(
    private val featureToggleManager: FeatureToggleManager
) {
    @KafkaListener(
        id = "StoreApplicationFeatureToggleUpdater",
        topics = [featureToggleTopic]
    )
    @RetryableTopic(
        attempts = "3",
        backoff = Backoff(value = 3000L),
    )
    fun updateFeatureToggle(payload: UpdateFeatureTogglePayload) {
        this.featureToggleManager.updateFeatureToggle(
            name = payload.name,
            toggle = payload.toggle
        )
    }

    companion object {
        const val featureToggleTopic = "event.backoffice.feature-toggle"
    }
}

data class UpdateFeatureTogglePayload(
    val name: String,
    val toggle: Boolean
)