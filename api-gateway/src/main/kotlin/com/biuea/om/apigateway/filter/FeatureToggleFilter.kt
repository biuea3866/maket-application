package com.biuea.om.apigateway.filter

import com.biuea.om.apigateway.infrastructure.cache.FeatureToggleManager
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class FeatureToggleFilter(
    private val featureToggleManager: FeatureToggleManager
): AbstractGatewayFilterFactory<FeatureToggleFilter.Config>() {
    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            exchange.attributes["features"] = featureToggleManager.getAllFeatures()
            chain.filter(exchange)
        }
    }
}