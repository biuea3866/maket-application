package com.biuea.om.navermockingapi.api

import com.biuea.om.navermockingapi.repository.entity.*
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

// 판매자 응답 DTO
data class SellerResponse(
    val sellerId: String,
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
    val price: BigDecimal,
    val salePrice: BigDecimal?,
    val stockQuantity: Int,
    val status: String,
    val description: String?,
    val mainImage: String?,
    val images: List<ProductImageResponse>,
    val options: List<ProductOptionResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(product: Product) = ProductResponse(
            productId = product.productId,
            sellerId = product.seller.sellerId,
            name = product.name,
            categoryId = product.category.categoryId,
            categoryName = product.category.name,
            price = product.price,
            salePrice = product.salePrice,
            stockQuantity = product.stockQuantity,
            status = product.status.name,
            description = product.description,
            mainImage = product.mainImage,
            images = product.images.map { ProductImageResponse.from(it) },
            options = product.options.map { ProductOptionResponse.from(it) },
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }
}

// 상품 목록 응답 (간단 버전)
data class ProductListResponse(
    val productId: String,
    val name: String,
    val price: BigDecimal,
    val salePrice: BigDecimal?,
    val stockQuantity: Int,
    val status: String,
    val mainImage: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(product: Product) = ProductListResponse(
            productId = product.productId,
            name = product.name,
            price = product.price,
            salePrice = product.salePrice,
            stockQuantity = product.stockQuantity,
            status = product.status.name,
            mainImage = product.mainImage,
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

// 주문 응답 DTO
data class OrderResponse(
    val orderId: String,
    val sellerId: String,
    val orderDate: LocalDateTime,
    val buyer: BuyerInfo,
    val receiver: ReceiverInfo,
    val paymentMethod: String,
    val totalAmount: BigDecimal,
    val orderStatus: String,
    val orderItems: List<OrderItemResponse>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(order: Order) = OrderResponse(
            orderId = order.orderId,
            sellerId = order.seller.sellerId,
            orderDate = order.orderDate,
            buyer = BuyerInfo(
                name = order.buyerName,
                email = order.buyerEmail,
                phone = order.buyerPhone
            ),
            receiver = ReceiverInfo(
                name = order.receiverName,
                phone = order.receiverPhone,
                zipcode = order.receiverZipcode,
                address = order.receiverAddress,
                addressDetail = order.receiverAddressDetail
            ),
            paymentMethod = order.paymentMethod,
            totalAmount = order.totalAmount,
            orderStatus = order.orderStatus.name,
            orderItems = order.orderItems.map { OrderItemResponse.from(it) },
            createdAt = order.createdAt
        )
    }
}

data class BuyerInfo(
    val name: String,
    val email: String?,
    val phone: String
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

// 카테고리 응답 DTO
data class CategoryResponse(
    val categoryId: String,
    val name: String,
    val parentId: String?,
    val level: Int,
    val isActive: Boolean,
    val children: List<CategoryResponse> = emptyList()
) {
    companion object {
        fun from(category: Category, children: List<CategoryResponse> = emptyList()) = CategoryResponse(
            categoryId = category.categoryId,
            name = category.name,
            parentId = category.parentId,
            level = category.level,
            isActive = category.isActive,
            children = children
        )
    }
}

// 통계 응답 DTO
data class SellerDashboardResponse(
    val totalProducts: Long,
    val totalOrders: Long,
    val pendingOrders: Long,
    val deliveringOrders: Long,
    val todaySales: BigDecimal,
    val monthSales: BigDecimal
)