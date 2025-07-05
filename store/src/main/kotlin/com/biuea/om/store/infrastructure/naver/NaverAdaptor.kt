package com.biuea.om.store.infrastructure.naver

import com.biuea.om.store.domain.entity.IntegrationPlatform
import com.biuea.om.store.domain.service.CancelStore
import com.biuea.om.store.domain.service.CancelStoreRequest
import com.biuea.om.store.domain.service.RegisterStore
import com.biuea.om.store.domain.service.RegisterStoreRequest
import org.springframework.stereotype.Component

@Component
class NaverAdaptor: RegisterStore, CancelStore {
    override val platform: IntegrationPlatform
        get() = IntegrationPlatform.NAVER

    override fun register(request: RegisterStoreRequest) {
        TODO("Not yet implemented")
    }

    override fun cancel(request: CancelStoreRequest) {
        TODO("Not yet implemented")
    }
}