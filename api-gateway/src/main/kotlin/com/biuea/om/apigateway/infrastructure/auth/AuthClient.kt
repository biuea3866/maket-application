package com.biuea.om.apigateway.infrastructure.auth

import com.biuea.om.apigateway.config.FeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "auth-client",
    url = "http://localhost:12000/auth",
    configuration = [FeignConfig::class]
)
interface AuthClient {
    @PostMapping(value = ["/validate/token"])
    fun validateToken(@RequestHeader("Authorization") authorization: String): String
}