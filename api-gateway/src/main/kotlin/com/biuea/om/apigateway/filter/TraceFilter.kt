package com.biuea.om.apigateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class TraceFilter: AbstractGatewayFilterFactory<TraceFilter.Config>() {
    class Config

    override fun apply(config: Config): GatewayFilter {
        TODO("Not yet implemented")
    }
}