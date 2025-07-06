package com.biuea.om.kakaomockingapi.common

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val apiKeyScheme = SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .name("X-API-KEY")
            .description("판매자 API 키")

        val kakaoIdScheme = SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .name("X-KAKAO-ID")
            .description("카카오 계정 ID")

        val securityRequirement = SecurityRequirement()
            .addList("apiKey")
            .addList("kakaoId")

        return OpenAPI()
            .info(
                Info()
                    .title("카카오 커머스 모킹 API")
                    .description("카카오 커머스 API를 모킹한 서버입니다")
                    .version("1.0.0")
            )
            .servers(listOf(
                Server()
                    .url("http://localhost:8082/kakao-api")
                    .description("로컬 개발 서버")
            ))
            .components(
                Components()
                    .addSecuritySchemes("apiKey", apiKeyScheme)
                    .addSecuritySchemes("kakaoId", kakaoIdScheme)
            )
            .addSecurityItem(securityRequirement)
    }
}

@Configuration
class CorsConfig {
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("X-Total-Count", "X-KAKAO-TOKEN")
            maxAge = 3600
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
