package com.biuea.om.apigateway.service

import com.biuea.om.apigateway.infrastructure.auth.AuthAdaptor
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authAdaptor: AuthAdaptor
) {
    fun authenticateAppToken(token: String): String {
        return authAdaptor.validateToken(token)
    }
}