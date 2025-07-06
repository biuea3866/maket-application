package com.biuea.om.store.infrastructure.cache

import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class LocalCacheConfiguration {
    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager().apply {
            setCaches(listOf(ConcurrentMapCache("feature-toggles")))
        }
        cacheManager.initializeCaches()

        return cacheManager
    }
}