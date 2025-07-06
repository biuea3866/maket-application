package com.biuea.om.apigateway.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LoggingFilter: AbstractGatewayFilterFactory<LoggingFilter.Config>() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val response = exchange.response

            logger.info(
                """
                    header: ${request.headers}
                    uri: ${request.uri}
                    method: ${request.method}
                    body: ${request.body}
                    query parameter: ${request.queryParams}
                """.trimIndent()
            )

            chain.filter(exchange).then(Mono.fromRunnable {
                logger.info(
                    """
                        response status code: ${response.statusCode}
                        response header: ${response.headers}    
                    """.trimIndent()
                )
            })
        }
    }
}