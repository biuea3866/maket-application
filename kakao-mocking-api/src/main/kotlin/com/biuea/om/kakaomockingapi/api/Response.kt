package com.biuea.om.kakaomockingapi.api

import com.biuea.om.kakaomockingapi.repository.entity.*
import java.math.BigDecimal
import java.time.LocalDateTime

// 공통 응답 래퍼
data class ApiResponse<T>(
    val code: String = "SUCCESS",
    val message: String = "성공",
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class ErrorResponse(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

// 페이징 응답
data class PageResponse<T>(
    val items: List<T>,
    val totalItems: Long,
    val totalPages: Int,
    val currentPage: Int,
    val itemsPerPage: Int
)

// 카카오 로그인 응답
data class KakaoLoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val kakaoAccountId: String,
    val nickname: String?,
    val profileImage: String?
)

// 판매자 응답 DTO
data class SellerResponse(
    val sellerId: String,
    val kakaoAccountId: String?,
    val businessName: String,
    val businessNumber: String,
    val representativeName: String,
    val email: String,
    val phone: String,
    val status: String,
    val apiKey: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(seller: Seller) = SellerResponse(
            sellerId = seller.sellerId,
            kakaoAccountId = seller.kakaoAccountId,
            businessName = seller.businessName,
            businessNumber = seller.businessNumber,
            representativeName = seller.representativeName,
            email = seller.email,
            phone = seller.phone,
            status = seller.status.name,
            apiKey = seller.apiKey,
            createdAt = seller.createdAt
        )
    }
}

// 상품 응답 DTO
data class ProductResponse(
    val productId: String,
    val sellerId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val brand: String?,
    val price: BigDecimal,
    val salePrice: BigDecimal?,
    val stockQuantity: Int,
    val status: String,
    val description: String?,
    val mainImage: String?,
    val isKakaoPay: Boolean,
    val shippingType: String,
    val shippingFee: BigDecimal,
    val images: List<ProductImageResponse>,
    val options: List<ProductOptionResponse>,
    val reviewCount: Long,
    val averageRating: Double?,
    val wishCount: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(product: Product, reviewCount: Long = 0, averageRating: Double? = null, wishCount: Long = 0) = ProductResponse(
            productId = product.productId,
            sellerId = product.seller.sellerId,
            name = product.name,
            categoryId = product.category.categoryId,
            categoryName = product.category.name,
            brand = product.brand,
            price = product.price,
            salePrice = product.salePrice,
            stockQuantity = product.stockQuantity,
            status = product.status.name,
            description = product.description,
            mainImage = product.mainImage,
            isKakaoPay = product.isKakaoPay,
            shippingType = product.shippingType.name,
            shippingFee = product.shippingFee,
            images = product.images.map { ProductImageResponse.from(it) },
            options = product.options.map { ProductOptionResponse.from(it) },
            reviewCount = reviewCount,
            averageRating = averageRating,
            wishCount = wishCount,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }
}

// 상품 목록 응답 (간단 버전)
data class ProductListResponse(
    val productId: String,
    val name: String,
    val brand: String?,
    val price: BigDecimal,
    val salePrice: BigDecimal?,
    val stockQuantity: Int,
    val status: String,
    val mainImage: String?,
    val isKakaoPay: Boolean,
    val shippingType: String,
    val reviewCount: Long,
    val averageRating: Double?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(product: Product, reviewCount: Long = 0, averageRating: Double? = null) = ProductListResponse(
            productId = product.productId,
            name = product.name,
            brand = product.brand,
            price = product.price,
            salePrice = product.salePrice,
            stockQuantity = product.stockQuantity,
            status = product.status.name,
            mainImage = product.mainImage,
            isKakaoPay = product.isKakaoPay,
            shippingType = product.shippingType.name,
            reviewCount = reviewCount,
            averageRating = averageRating,
            createdAt = product.createdAt
        )
    }
}

data class ProductImageResponse(
    val imageUrl: String,
    val imageOrder: Int
) {
    companion object {
        fun from(image: ProductImage) = ProductImageResponse(
            imageUrl = image.imageUrl,
            imageOrder = image.imageOrder
        )
    }
}

data class ProductOptionResponse(
    val optionId: String,
    val optionName: String,
    val optionValue: String,
    val additionalPrice: BigDecimal,
    val stockQuantity: Int,
    val useYn: Boolean
) {
    companion object {
        fun from(option: ProductOption) = ProductOptionResponse(
            optionId = option.optionId,
            optionName = option.optionName,
            optionValue = option.optionValue,
            additionalPrice = option.additionalPrice,
            stockQuantity = option.stockQuantity,
            useYn = option.useYn
        )
    }
}

// 장바구니 응답
data class CartResponse(
    val cartId: String,
    val product: CartProductResponse,
    val option: ProductOptionResponse?,
    val quantity: Int,
    val isSelected: Boolean,
    val totalPrice: BigDecimal,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(cart: Cart): CartResponse {
            val productPrice = cart.product.salePrice ?: cart.product.price
            val optionPrice = cart.option?.additionalPrice ?: BigDecimal.ZERO
            val totalPrice = (productPrice.add(optionPrice)).multiply(BigDecimal(cart.quantity))

            return CartResponse(
                cartId = cart.cartId,
                product = CartProductResponse.from(cart.product),
                option = cart.option?.let { ProductOptionResponse.from(it) },
                quantity = cart.quantity,
                isSelected = cart.isSelected,
                totalPrice = totalPrice,
                createdAt = cart.createdAt
            )
        }
    }
}

data class CartProductResponse(
    val productId: String,
    val name: String,
    val price: BigDecimal,
    val salePrice: BigDecimal?,
    val mainImage: String?,
    val isKakaoPay: Boolean,
    val shippingType: String,
    val shippingFee: BigDecimal
) {
    companion object {
        fun from(product: Product) = CartProductResponse(
            productId = product.productId,
            name = product.name,
            price = product.price,
            salePrice = product.salePrice,
            mainImage = product.mainImage,
            isKakaoPay = product.isKakaoPay,
            shippingType = product.shippingType.name,
            shippingFee = product.shippingFee
        )
    }
}

// 찜 목록 응답
data class WishlistResponse(
    val product: ProductListResponse,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(wishlist: Wishlist, reviewCount: Long = 0, averageRating: Double? = null) = WishlistResponse(
            product = ProductListResponse.from(wishlist.product, reviewCount, averageRating),
            createdAt = wishlist.createdAt
        )
    }
}

// 주문 응답 DTO
data class OrderResponse(
    val orderId: String,
    val sellerId: String,
    val kakaoOrderId: String?,
    val orderDate: LocalDateTime,
    val buyer: BuyerInfo,
    val receiver: ReceiverInfo,
    val paymentMethod: String,
    val totalAmount: BigDecimal,
    val deliveryMessage: String?,
    val orderStatus: String,
    val orderItems: List<OrderItemResponse>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(order: Order) = OrderResponse(
            orderId = order.orderId,
            sellerId = order.seller.sellerId,
            kakaoOrderId = order.kakaoOrderId,
            orderDate = order.orderDate,
            buyer = BuyerInfo(
                name = order.buyerName,
                email = order.buyerEmail,
                phone = order.buyerPhone,
                kakaoId = order.buyerKakaoId
            ),
            receiver = ReceiverInfo(
                name = order.receiverName,
                phone = order.receiverPhone,
                zipcode = order.receiverZipcode,
                address = order.receiverAddress,
                addressDetail = order.receiverAddressDetail
            ),
            paymentMethod = order.paymentMethod.name,
            totalAmount = order.totalAmount,
            deliveryMessage = order.deliveryMessage,
            orderStatus = order.orderStatus.name,
            orderItems = order.orderItems.map { OrderItemResponse.from(it) },
            createdAt = order.createdAt
        )
    }
}

data class BuyerInfo(
    val name: String,
    val email: String?,
    val phone: String,
    val kakaoId: String?
)

data class ReceiverInfo(
    val name: String,
    val phone: String,
    val zipcode: String,
    val address: String,
    val addressDetail: String?
)

data class OrderItemResponse(
    val productId: String,
    val productName: String,
    val optionId: String?,
    val optionValue: String?,
    val quantity: Int,
    val price: BigDecimal,
    val status: String
) {
    companion object {
        fun from(item: OrderItem) = OrderItemResponse(
            productId = item.product.productId,
            productName = item.product.name,
            optionId = item.option?.optionId,
            optionValue = item.option?.let { "${it.optionName}: ${it.optionValue}" },
            quantity = item.quantity,
            price = item.price,
            status = item.status.name
        )
    }
}

// 리뷰 응답
data class ReviewResponse(
    val reviewId: String,
    val productId: String,
    val orderId: String,
    val buyerKakaoId: String,
    val buyerName: String,
    val rating: Int,
    val content: String?,
    val isPhotoReview: Boolean,
    val images: List<ReviewImageResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(review: Review) = ReviewResponse(
            reviewId = review.reviewId,
            productId = review.product.productId,
            orderId = review.order.orderId,
            buyerKakaoId = review.buyerKakaoId,
            buyerName = review.buyerName,
            rating = review.rating,
            content = review.content,
            isPhotoReview = review.isPhotoReview,
            images = review.images.map { ReviewImageResponse.from(it) },
            createdAt = review.createdAt,
            updatedAt = review.updatedAt
        )
    }
}

data class ReviewImageResponse(
    val imageUrl: String,
    val imageOrder: Int
) {
    companion object {
        fun from(image: ReviewImage) = ReviewImageResponse(
            imageUrl = image.imageUrl,
            imageOrder = image.imageOrder
        )
    }
}

// 카테고리 응답 DTO
data class CategoryResponse(
    val categoryId: String,
    val name: String,
    val parentId: String?,
    val level: Int,
    val displayOrder: Int,
    val isActive: Boolean,
    val children: List<CategoryResponse> = emptyList()
) {
    companion object {
        fun from(category: Category, children: List<CategoryResponse> = emptyList()) = CategoryResponse(
            categoryId = category.categoryId,
            name = category.name,
            parentId = category.parentId,
            level = category.level,
            displayOrder = category.displayOrder,
            isActive = category.isActive,
            children = children
        )
    }
}

// 알림톡 응답
data class KakaoNotificationResponse(
    val notificationId: String,
    val orderId: String?,
    val receiverPhone: String,
    val templateCode: String,
    val messageContent: String,
    val sendStatus: String,
    val sentAt: LocalDateTime?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(notification: KakaoNotification) = KakaoNotificationResponse(
            notificationId = notification.notificationId,
            orderId = notification.order?.orderId,
            receiverPhone = notification.receiverPhone,
            templateCode = notification.templateCode,
            messageContent = notification.messageContent,
            sendStatus = notification.sendStatus.name,
            sentAt = notification.sentAt,
            createdAt = notification.createdAt
        )
    }
}

// 판매자 대시보드 응답
data class SellerDashboardResponse(
    val totalProducts: Long,
    val activeProducts: Long,
    val totalOrders: Long,
    val pendingOrders: Long,
    val deliveringOrders: Long,
    val todaySales: BigDecimal,
    val monthSales: BigDecimal,
    val kakaoPayRatio: Double
)