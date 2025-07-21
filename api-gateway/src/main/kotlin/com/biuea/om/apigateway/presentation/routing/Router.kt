package com.biuea.om.apigateway.presentation.routing

import com.biuea.om.apigateway.filter.AuthenticationFilter
import com.biuea.om.apigateway.filter.FeatureToggleFilter
import com.biuea.om.apigateway.filter.LoggingFilter
import com.biuea.om.apigateway.filter.TraceFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.RouteLocatorDsl
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Router(
    private val authenticationFilter: AuthenticationFilter,
    private val loggingFilter: LoggingFilter,
    private val traceFilter: TraceFilter,
    private val featureToggleFilter: FeatureToggleFilter
) {
    @Bean
    fun routingPattern(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes { appRouting(this) }
    }

    private fun appRouting(builder: RouteLocatorDsl) {
        builder.route(id = "app-api-routing") {
            path("/app")
                .filters { f ->
                    f.filter(loggingFilter.apply(LoggingFilter.Config()))
                    f.filter(traceFilter.apply(TraceFilter.Config()))
                    f.filter(authenticationFilter.apply(AuthenticationFilter.Config()))
                    f.filter(featureToggleFilter.apply(FeatureToggleFilter.Config()))
                    f.rewritePath("/api/app", "/app")
                }
                .uri("http://localhost:21000")
        }
    }
}