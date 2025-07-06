package com.biuea.om.apigateway.service

import com.biuea.om.apigateway.infrastructure.auth.AuthClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
    private val authClient: AuthClient
) {
    fun authenticateAppToken(token: String): String {
        return authClient.validateToken(token)
    }
}