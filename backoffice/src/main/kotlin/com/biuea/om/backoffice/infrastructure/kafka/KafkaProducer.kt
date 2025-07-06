package com.biuea.om.backoffice.infrastructure.kafka

import com.biuea.om.backoffice.domain.feature.FeatureToggle
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    fun publishFeatureToggle(featureToggle: FeatureToggle) {
        this.kafkaTemplate.send(
            featureToggleTopic,
            "FEATURE_TOGGLE::${featureToggle.id}",
                    PublishFeatureTogglePayload.of(featureToggle)
        )
    }

    companion object {
        val featureToggleTopic = "event.backoffice.feature-toggle"
    }
}

data class PublishFeatureTogglePayload(
    val name: String,
    val toggle: Boolean
) {
    companion object {
        fun of(featureToggle: FeatureToggle): PublishFeatureTogglePayload {
            return PublishFeatureTogglePayload(
                name = featureToggle.name,
                toggle = featureToggle.toggle
            )
        }
    }
}