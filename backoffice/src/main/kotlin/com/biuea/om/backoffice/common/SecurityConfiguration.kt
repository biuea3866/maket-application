package com.biuea.om.backoffice.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsUtils

@Configuration
class SecurityConfiguration {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.authorizeHttpRequests {
            it.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            it.anyRequest().permitAll()
        }.cors(withDefaults())
            .csrf { it.disable() }
            .httpBasic(withDefaults())
            .build()
    }
}