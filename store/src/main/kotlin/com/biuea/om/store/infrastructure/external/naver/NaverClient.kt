package com.biuea.om.store.infrastructure.external.naver

import com.biuea.om.store.domain.value.StoreRegistrationRequestInfo
import com.biuea.om.store.infrastructure.external.config.FeignConfig
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDateTime
import kotlin.String

@FeignClient(
    name = "naverClient",
    url = "http://localhost:10000/naver-api/",
    configuration = [FeignConfig::class]
)
interface NaverClient {
    @PostMapping(value = ["/v1/seller/register"])
    fun registerStore(@RequestBody body: RegisterNaverStoreBody): RegisterNaverStoreResponse
}

data class RegisterNaverStoreBody(
    @field:NotBlank(message = "사업자명은 필수입니다")
    @field:Size(max = 200)
    val businessName: String,

    @field:NotBlank(message = "사업자번호는 필수입니다")
    @field:Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자번호 형식이 올바르지 않습니다")
    val businessNumber: String,

    @field:NotBlank(message = "대표자명은 필수입니다")
    @field:Size(max = 100)
    val representativeName: String,

    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "이메일 형식이 올바르지 않습니다")
    val email: String,

    @field:NotBlank(message = "전화번호는 필수입니다")
    @field:Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "전화번호 형식이 올바르지 않습니다")
    val phone: String
) {
    companion object {
        fun of(info: StoreRegistrationRequestInfo): RegisterNaverStoreBody {
            return RegisterNaverStoreBody(
                businessName = info.businessName,
                businessNumber = info.businessNumber,
                representativeName = info.representativeName,
                email = info.email,
                phone = info.phone,
            )
        }
    }
}

data class RegisterNaverStoreResponse(
    val sellerId: String,
    val businessName: String,
    val businessNumber: String,
    val representativeName: String,
    val email: String,
    val phone: String,
    val status: String,
    val apiKey: String,
    val createdAt: LocalDateTime
)