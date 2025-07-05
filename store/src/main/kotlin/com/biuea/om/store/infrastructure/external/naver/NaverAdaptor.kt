package com.biuea.om.store.infrastructure.external.naver

import com.biuea.om.store.domain.entity.IntegrationPlatform
import com.biuea.om.store.domain.service.CancelStore
import com.biuea.om.store.domain.service.CancelStoreRequest
import com.biuea.om.store.domain.service.RegisterStore
import com.biuea.om.store.domain.value.StoreRegistrationInfo
import com.biuea.om.store.domain.value.StoreRegistrationRequestInfo
import org.springframework.stereotype.Component

@Component
class NaverAdaptor(
    private val client: NaverClient
): RegisterStore, CancelStore {
    override val platform: IntegrationPlatform
        get() = IntegrationPlatform.NAVER

    override fun register(registrationInformation: StoreRegistrationRequestInfo): StoreRegistrationInfo {
        val response = client.registerStore(RegisterNaverStoreBody.of(registrationInformation))

        return StoreRegistrationInfo(
            platform = this.platform,
            platformId = response.sellerId,
            status = response.status
        )
    }

    override fun cancel(request: CancelStoreRequest) {
        TODO("Not yet implemented")
    }
}