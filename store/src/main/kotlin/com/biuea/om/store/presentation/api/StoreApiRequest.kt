package com.biuea.om.store.presentation.api

import com.biuea.om.store.domain.entity.IntegrationPlatform

data class RegisterStoreRequest(
    val businessName: String,
    val businessNumber: String,
    val representativeName: String,
    val email: String,
    val phone: String,
    val description: String?,
    val platform: IntegrationPlatform
)