package com.biuea.om.store.domain.value

import com.biuea.om.store.domain.entity.IntegrationPlatform
import com.biuea.om.store.domain.entity.StoreStatus

data class StoreRegistrationRequestInfo(
    val businessName: String,
    val businessNumber: String,
    val representativeName: String,
    val email: String,
    val phone: String
) {
}

data class StoreRegistrationInfo(
    val platformId: String,
    private val status: String,
    val platform: IntegrationPlatform
) {
    val integrationStatus: StoreStatus get() = when(this.platform) {
        IntegrationPlatform.NAVER -> when(this.status) {
            "PENDING",
            "SUSPENDED" -> StoreStatus.REGISTER
            "APPROVED" -> StoreStatus.CONFIRM
            "REJECTED" -> StoreStatus.NOT_CONFIRM
            else -> throw IllegalStateException()
        }
        IntegrationPlatform.KAKAO -> TODO()
        IntegrationPlatform.COUPANG -> TODO()
        IntegrationPlatform.GMARKET -> TODO()
        IntegrationPlatform.CARROT -> TODO()
        IntegrationPlatform.OPEN_MARKET -> TODO()
    }
}