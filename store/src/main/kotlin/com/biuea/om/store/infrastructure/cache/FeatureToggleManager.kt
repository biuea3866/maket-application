package com.biuea.om.store.infrastructure.cache

import com.biuea.om.store.common.FeatureToggle
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.get
import org.springframework.stereotype.Component
import kotlin.collections.forEach

@Component
class FeatureToggleManager(
    private val cacheManager: CacheManager
) {
    private val features: MutableMap<String, Boolean> = mutableMapOf()

    @CacheEvict(value = ["feature-toggles"], allEntries = true)
    fun clear() {
        this.features.clear()
    }

    @CachePut(value = ["feature-toggles"], key = "#name")
    fun updateFeatureToggle(
        name: String,
        toggle: Boolean
    ) {
        val feature = this.features[name]

        if (feature != null) return

        this.features[name] = toggle
        println("update features: ${name}, ${toggle}")
    }

    @CachePut(value = ["feature-toggles"])
    fun updateFeatureToggles(features: Map<String, Boolean>) {
        features.forEach { key, value -> this.features[key] = value }
        println("update features: ${features}")
    }

    @Cacheable(value = ["feature-toggles"])
    fun getAllFeatures(): Map<String, Boolean> {
        return this.features
    }

    @Cacheable(value = ["feature-toggles"], key = "#name")
    fun getFeature(name: String): Boolean {
        return this.features[name] == true
    }
}