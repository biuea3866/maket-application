package com.biuea.om.navermockingapi.api

import jakarta.validation.constraints.*
import java.math.BigDecimal

// 판매자 입점 신청 DTO
data class SellerRegistrationRequest(
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
)

// 상품 등록 DTO
data class ProductCreateRequest(
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(max = 500)
    val name: String,

    @field:NotBlank(message = "카테고리 ID는 필수입니다")
    val categoryId: String,

    @field:NotNull(message = "가격은 필수입니다")
    @field:Positive(message = "가격은 0보다 커야 합니다")
    val price: BigDecimal,

    val salePrice: BigDecimal? = null,

    @field:NotNull(message = "재고수량은 필수입니다")
    @field:PositiveOrZero(message = "재고수량은 0 이상이어야 합니다")
    val stockQuantity: Int,

    val description: String? = null,
    val detailContent: String? = null,
    val mainImage: String? = null,
    val images: List<ProductImageRequest> = emptyList(),
    val options: List<ProductOptionRequest> = emptyList()
)

data class ProductImageRequest(
    @field:NotBlank(message = "이미지 URL은 필수입니다")
    val imageUrl: String,

    @field:PositiveOrZero(message = "이미지 순서는 0 이상이어야 합니다")
    val imageOrder: Int = 0
)

data class ProductOptionRequest(
    @field:NotBlank(message = "옵션명은 필수입니다")
    val optionName: String,

    @field:NotBlank(message = "옵션값은 필수입니다")
    val optionValue: String,

    @field:PositiveOrZero(message = "추가금액은 0 이상이어야 합니다")
    val additionalPrice: BigDecimal = BigDecimal.ZERO,

    @field:PositiveOrZero(message = "재고수량은 0 이상이어야 합니다")
    val stockQuantity: Int = 0
)

// 상품 수정 DTO
data class ProductUpdateRequest(
    val name: String? = null,
    val categoryId: String? = null,
    val price: BigDecimal? = null,
    val salePrice: BigDecimal? = null,
    val stockQuantity: Int? = null,
    val status: String? = null,
    val description: String? = null,
    val detailContent: String? = null,
    val mainImage: String? = null
)

// 주문 조회 필터
data class OrderSearchRequest(
    val orderStatus: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val buyerName: String? = null,
    val page: Int = 1,
    val size: Int = 20
)

// 주문 상태 변경
data class OrderStatusUpdateRequest(
    @field:NotBlank(message = "주문 상태는 필수입니다")
    val status: String,

    val reason: String? = null
)

// 상품 검색 필터
data class ProductSearchRequest(
    val status: String? = null,
    val categoryId: String? = null,
    val keyword: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val page: Int = 1,
    val size: Int = 20
)