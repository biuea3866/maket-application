package com.biuea.om.apigateway.filter

import com.biuea.om.apigateway.service.AuthenticationService
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class AuthenticationFilter(
    private val authenticationService: AuthenticationService
): AbstractGatewayFilterFactory<AuthenticationFilter.Config>() {
    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val token = exchange.request.headers["Authorization"]?.firstOrNull()

            when (token == null) {
                true -> {
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    exchange.response.setComplete()
                }
                false -> {
                    runCatching {
                        exchange.attributes["userId"] = authenticationService.authenticateAppToken(token)
                        chain.filter(exchange)
                    }.getOrElse {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        exchange.response.setComplete()
                    }
                }
            }
        }
    }
}