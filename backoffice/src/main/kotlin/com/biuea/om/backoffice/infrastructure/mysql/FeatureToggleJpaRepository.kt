package com.biuea.om.backoffice.infrastructure.mysql

import com.biuea.om.backoffice.domain.feature.FeatureToggle
import org.springframework.data.jpa.repository.JpaRepository

interface FeatureToggleJpaRepository: JpaRepository<FeatureToggle, Long> {
    fun findByName(name: String): FeatureToggle?
}