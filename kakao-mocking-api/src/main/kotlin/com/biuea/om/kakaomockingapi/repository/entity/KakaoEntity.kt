package com.biuea.om.kakaomockingapi.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.math.BigDecimal

@Entity
@Table(name = "sellers")
@EntityListeners(AuditingEntityListener::class)
data class Seller(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "seller_id", unique = true, nullable = false)
    val sellerId: String,

    @Column(name = "kakao_account_id", unique = true)
    val kakaoAccountId: String? = null,

    @Column(name = "business_name", nullable = false)
    val businessName: String,

    @Column(name = "business_number", nullable = false)
    val businessNumber: String,

    @Column(name = "representative_name", nullable = false)
    val representativeName: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val phone: String,

    @Enumerated(EnumType.STRING)
    val status: SellerStatus = SellerStatus.PENDING,

    @Column(name = "access_token", length = 500)
    val accessToken: String? = null,

    @Column(name = "refresh_token", length = 500)
    val refreshToken: String? = null,

    @Column(name = "api_key", nullable = false)
    val apiKey: String,

    @Column(name = "api_secret", nullable = false)
    val apiSecret: String,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class SellerStatus {
    PENDING, APPROVED, REJECTED, SUSPENDED
}

@Entity
@Table(name = "categories")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "category_id", unique = true, nullable = false)
    val categoryId: String,

    @Column(nullable = false)
    val name: String,

    @Column(name = "parent_id")
    val parentId: String? = null,

    @Column(nullable = false)
    val level: Int,

    @Column(name = "display_order")
    val displayOrder: Int = 0,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener::class)
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "product_id", unique = true, nullable = false)
    val productId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "seller_id")
    val seller: Seller,

    @Column(nullable = false, length = 500)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    val category: Category,

    @Column
    val brand: String? = null,

    @Column(nullable = false, precision = 12, scale = 0)
    val price: BigDecimal,

    @Column(name = "sale_price", precision = 12, scale = 0)
    val salePrice: BigDecimal? = null,

    @Column(name = "stock_quantity")
    val stockQuantity: Int = 0,

    @Enumerated(EnumType.STRING)
    val status: ProductStatus = ProductStatus.SALE,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "detail_content", columnDefinition = "TEXT")
    val detailContent: String? = null,

    @Column(name = "main_image", length = 500)
    val mainImage: String? = null,

    @Column(name = "is_kakao_pay")
    val isKakaoPay: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_type")
    val shippingType: ShippingType = ShippingType.PAID,

    @Column(name = "shipping_fee", precision = 10, scale = 0)
    val shippingFee: BigDecimal = BigDecimal("3000"),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val images: MutableList<ProductImage> = mutableListOf(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val options: MutableList<ProductOption> = mutableListOf(),

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    val reviews: MutableList<Review> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ProductStatus {
    SALE, SUSPEND, OUTOFSTOCK, CLOSE
}

enum class ShippingType {
    FREE, CONDITIONAL_FREE, PAID
}

@Entity
@Table(name = "product_images")
data class ProductImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    val product: Product,

    @Column(name = "image_url", nullable = false, length = 500)
    val imageUrl: String,

    @Column(name = "image_order")
    val imageOrder: Int = 0,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "product_options")
data class ProductOption(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "option_id", unique = true, nullable = false)
    val optionId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    val product: Product,

    @Column(name = "option_name", nullable = false)
    val optionName: String,

    @Column(name = "option_value", nullable = false)
    val optionValue: String,

    @Column(name = "additional_price", precision = 10, scale = 0)
    val additionalPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "stock_quantity")
    val stockQuantity: Int = 0,

    @Column(name = "use_yn")
    val useYn: Boolean = true,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "order_id", unique = true, nullable = false)
    val orderId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "seller_id")
    val seller: Seller,

    @Column(name = "kakao_order_id")
    val kakaoOrderId: String? = null,

    @Column(name = "order_date", nullable = false)
    val orderDate: LocalDateTime,

    @Column(name = "buyer_name", nullable = false)
    val buyerName: String,

    @Column(name = "buyer_email")
    val buyerEmail: String? = null,

    @Column(name = "buyer_phone", nullable = false)
    val buyerPhone: String,

    @Column(name = "buyer_kakao_id")
    val buyerKakaoId: String? = null,

    @Column(name = "receiver_name", nullable = false)
    val receiverName: String,

    @Column(name = "receiver_phone", nullable = false)
    val receiverPhone: String,

    @Column(name = "receiver_zipcode", nullable = false)
    val receiverZipcode: String,

    @Column(name = "receiver_address", nullable = false, length = 500)
    val receiverAddress: String,

    @Column(name = "receiver_address_detail")
    val receiverAddressDetail: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    val paymentMethod: PaymentMethod = PaymentMethod.KAKAO_PAY,

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 0)
    val totalAmount: BigDecimal,

    @Column(name = "delivery_message", length = 500)
    val deliveryMessage: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    val orderStatus: OrderStatus = OrderStatus.PAYMENT_WAITING,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val orderItems: MutableList<OrderItem> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class PaymentMethod {
    KAKAO_PAY, CARD, BANK_TRANSFER, PHONE
}

enum class OrderStatus {
    PAYMENT_WAITING, PAYED, DELIVERING, DELIVERED,
    PURCHASE_DECIDED, EXCHANGED, CANCELED, RETURNED
}

@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    val order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", referencedColumnName = "option_id")
    val option: ProductOption? = null,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = false, precision = 10, scale = 0)
    val price: BigDecimal,

    @Enumerated(EnumType.STRING)
    val status: OrderStatus = OrderStatus.PAYMENT_WAITING,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "carts")
@EntityListeners(AuditingEntityListener::class)
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "cart_id", unique = true, nullable = false)
    val cartId: String,

    @Column(name = "buyer_kakao_id")
    val buyerKakaoId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", referencedColumnName = "option_id")
    val option: ProductOption? = null,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "is_selected")
    val isSelected: Boolean = true,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "wishlists")
data class Wishlist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "buyer_kakao_id", nullable = false)
    val buyerKakaoId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    val product: Product,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener::class)
data class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "review_id", unique = true, nullable = false)
    val reviewId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    val order: Order,

    @Column(name = "buyer_kakao_id", nullable = false)
    val buyerKakaoId: String,

    @Column(name = "buyer_name", nullable = false)
    val buyerName: String,

    @Column(nullable = false)
    val rating: Int,

    @Column(columnDefinition = "TEXT")
    val content: String? = null,

    @Column(name = "is_photo_review")
    val isPhotoReview: Boolean = false,

    @OneToMany(mappedBy = "review", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val images: MutableList<ReviewImage> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "review_images")
data class ReviewImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", referencedColumnName = "review_id")
    val review: Review,

    @Column(name = "image_url", nullable = false, length = 500)
    val imageUrl: String,

    @Column(name = "image_order")
    val imageOrder: Int = 0,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "kakao_notifications")
data class KakaoNotification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "notification_id", unique = true, nullable = false)
    val notificationId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    val order: Order? = null,

    @Column(name = "receiver_phone", nullable = false)
    val receiverPhone: String,

    @Column(name = "template_code", nullable = false)
    val templateCode: String,

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    val messageContent: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "send_status")
    val sendStatus: NotificationStatus = NotificationStatus.PENDING,

    @Column(name = "sent_at")
    val sentAt: LocalDateTime? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class NotificationStatus {
    PENDING, SUCCESS, FAILED
}