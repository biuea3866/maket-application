package com.biuea.om.backoffice.application

import com.biuea.om.backoffice.domain.feature.FeatureToggle
import com.biuea.om.backoffice.infrastructure.kafka.KafkaProducer
import com.biuea.om.backoffice.infrastructure.mysql.FeatureToggleJpaRepository
import com.biuea.om.backoffice.presentation.api.GetFeatureResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeatureToggleService(
    private val featureToggleRepository: FeatureToggleJpaRepository,
    private val kafkaProducer: KafkaProducer
) {
    @Transactional
    fun registerFeature(
        name: String,
        toggle: Boolean
    ) {
        val featureToggle = FeatureToggle.create(
            name = name,
            toggle = toggle
        )
        featureToggleRepository.save(featureToggle)
        kafkaProducer.publishFeatureToggle(featureToggle)
    }

    @Transactional
    fun getAll(): List<GetFeatureResponse> {
        return featureToggleRepository.findAll().map(GetFeatureResponse::of)
    }
}