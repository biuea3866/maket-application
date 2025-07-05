package com.biuea.om.kakaomockingapi.api

import jakarta.validation.constraints.*
import java.math.BigDecimal

// 카카오 OAuth 로그인 요청
data class KakaoLoginRequest(
    @field:NotBlank(message = "인증 코드는 필수입니다")
    val authorizationCode: String
)

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
    val phone: String,

    val kakaoAccountId: String? = null
)

// 상품 등록 DTO
data class ProductCreateRequest(
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(max = 500)
    val name: String,

    @field:NotBlank(message = "카테고리 ID는 필수입니다")
    val categoryId: String,

    val brand: String? = null,

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

    @field:NotNull(message = "카카오페이 사용여부는 필수입니다")
    val isKakaoPay: Boolean = true,

    @field:NotNull(message = "배송 타입은 필수입니다")
    val shippingType: String = "PAID",

    val shippingFee: BigDecimal? = BigDecimal("3000"),

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
    val brand: String? = null,
    val price: BigDecimal? = null,
    val salePrice: BigDecimal? = null,
    val stockQuantity: Int? = null,
    val status: String? = null,
    val description: String? = null,
    val detailContent: String? = null,
    val mainImage: String? = null,
    val isKakaoPay: Boolean? = null,
    val shippingType: String? = null,
    val shippingFee: BigDecimal? = null
)

// 장바구니 추가 요청
data class CartAddRequest(
    @field:NotBlank(message = "상품 ID는 필수입니다")
    val productId: String,

    val optionId: String? = null,

    @field:NotNull(message = "수량은 필수입니다")
    @field:Positive(message = "수량은 0보다 커야 합니다")
    val quantity: Int = 1
)

// 장바구니 수정 요청
data class CartUpdateRequest(
    @field:Positive(message = "수량은 0보다 커야 합니다")
    val quantity: Int? = null,

    val isSelected: Boolean? = null
)

// 찜 추가/삭제 요청
data class WishlistRequest(
    @field:NotBlank(message = "상품 ID는 필수입니다")
    val productId: String
)

// 주문 생성 요청
data class OrderCreateRequest(
    @field:NotEmpty(message = "주문 상품이 없습니다")
    val items: List<OrderItemRequest>,

    @field:NotBlank(message = "구매자명은 필수입니다")
    val buyerName: String,

    val buyerEmail: String? = null,

    @field:NotBlank(message = "구매자 전화번호는 필수입니다")
    val buyerPhone: String,

    @field:NotBlank(message = "수령자명은 필수입니다")
    val receiverName: String,

    @field:NotBlank(message = "수령자 전화번호는 필수입니다")
    val receiverPhone: String,

    @field:NotBlank(message = "우편번호는 필수입니다")
    val receiverZipcode: String,

    @field:NotBlank(message = "주소는 필수입니다")
    val receiverAddress: String,

    val receiverAddressDetail: String? = null,

    @field:NotBlank(message = "결제수단은 필수입니다")
    val paymentMethod: String = "KAKAO_PAY",

    val deliveryMessage: String? = null,

    val kakaoOrderId: String? = null
)

data class OrderItemRequest(
    @field:NotBlank(message = "상품 ID는 필수입니다")
    val productId: String,

    val optionId: String? = null,

    @field:NotNull(message = "수량은 필수입니다")
    @field:Positive(message = "수량은 0보다 커야 합니다")
    val quantity: Int
)

// 주문 조회 필터
data class OrderSearchRequest(
    val orderStatus: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val buyerName: String? = null,
    val paymentMethod: String? = null,
    val page: Int = 1,
    val size: Int = 20
)

// 주문 상태 변경
data class OrderStatusUpdateRequest(
    @field:NotBlank(message = "주문 상태는 필수입니다")
    val status: String,

    val reason: String? = null
)

// 리뷰 작성 요청
data class ReviewCreateRequest(
    @field:NotBlank(message = "주문 ID는 필수입니다")
    val orderId: String,

    @field:NotBlank(message = "상품 ID는 필수입니다")
    val productId: String,

    @field:NotNull(message = "평점은 필수입니다")
    @field:Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @field:Max(value = 5, message = "평점은 5점 이하여야 합니다")
    val rating: Int,

    val content: String? = null,

    val imageUrls: List<String> = emptyList()
)

// 리뷰 수정 요청
data class ReviewUpdateRequest(
    val rating: Int? = null,
    val content: String? = null
)

// 상품 검색 필터
data class ProductSearchRequest(
    val status: String? = null,
    val categoryId: String? = null,
    val keyword: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val isKakaoPay: Boolean? = null,
    val shippingType: String? = null,
    val page: Int = 1,
    val size: Int = 20
)

// 카카오톡 알림 발송 요청
data class KakaoNotificationRequest(
    @field:NotBlank(message = "수신자 전화번호는 필수입니다")
    val receiverPhone: String,

    @field:NotBlank(message = "템플릿 코드는 필수입니다")
    val templateCode: String,

    val orderId: String? = null,

    val templateParams: Map<String, String> = emptyMap()
)