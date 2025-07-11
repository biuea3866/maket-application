package com.biuea.om.store.infrastructure.external.config

import feign.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {
    @Bean
    fun feignLoggerLevel(): Logger.Level = Logger.Level.FULL
}