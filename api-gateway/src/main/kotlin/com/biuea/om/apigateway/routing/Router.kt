package com.biuea.om.apigateway.routing

import com.biuea.om.apigateway.filter.AuthenticationFilter
import com.biuea.om.apigateway.filter.LoggingFilter
import com.biuea.om.apigateway.filter.TraceFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.RouteLocatorDsl
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.apply

@Configuration
class Router(
    private val authenticationFilter: AuthenticationFilter,
    private val loggingFilter: LoggingFilter,
    private val traceFilter: TraceFilter
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
                    f.rewritePath("/app", "/api/app")
                }
                .uri("http://localhost:8081")
        }
    }
}