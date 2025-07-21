package com.biuea.om.apigateway.infrastructure.auth

import org.springframework.stereotype.Component

@Component
class AuthAdaptor(
    private val authClient: AuthClient
) {
    fun validateToken(token: String): String = authClient.validateToken(token)
}