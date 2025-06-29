package com.biuea.om.navermockingapi.repository.entity

import com.biuea.om.navermockingapi.api.ProductUpdateRequest
import org.springframework.data.annotation.CreatedDate
import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.math.BigDecimal

@Entity
@Table(name = "sellers")
@EntityListeners(AuditingEntityListener::class)
class Seller(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "seller_id", unique = true, nullable = false)
    val sellerId: String,

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
    var status: SellerStatus = SellerStatus.PENDING,

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
) {
    fun approve() {
        this.status = SellerStatus.APPROVED
    }
}

enum class SellerStatus {
    PENDING, APPROVED, REJECTED, SUSPENDED
}

@Entity
@Table(name = "categories")
class Category(
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

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener::class)
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "product_id", unique = true, nullable = false)
    val productId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "seller_id")
    val seller: Seller,

    @Column(nullable = false, length = 500)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    var category: Category,

    @Column(nullable = false, precision = 12, scale = 0)
    var price: BigDecimal,

    @Column(name = "sale_price", precision = 12, scale = 0)
    var salePrice: BigDecimal? = null,

    @Column(name = "stock_quantity")
    var stockQuantity: Int = 0,

    @Enumerated(EnumType.STRING)
    var status: ProductStatus = ProductStatus.SALE,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "detail_content", columnDefinition = "TEXT")
    var detailContent: String? = null,

    @Column(name = "main_image", length = 500)
    var mainImage: String? = null,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val images: MutableList<ProductImage> = mutableListOf(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val options: MutableList<ProductOption> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(
        dto: ProductUpdateRequest,
        category: Category?
    ) {
        dto.name?.let { this.name = it }
        dto.price?.let { this.price = it }
        dto.salePrice?.let { this.salePrice = it }
        dto.stockQuantity?.let { this.stockQuantity = it }
        dto.description?.let { this.description = it }
        dto.detailContent?.let { this.detailContent = it }
        dto.mainImage?.let { this.mainImage = it }
        dto.status?.let { this.status = ProductStatus.valueOf(it) }
        category?.let { this.category = category }
    }
}

enum class ProductStatus {
    SALE, SUSPEND, OUTOFSTOCK, CLOSE
}

@Entity
@Table(name = "product_images")
class ProductImage(
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
class ProductOption(
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
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "order_id", unique = true, nullable = false)
    val orderId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "seller_id")
    val seller: Seller,

    @Column(name = "order_date", nullable = false)
    val orderDate: LocalDateTime,

    @Column(name = "buyer_name", nullable = false)
    val buyerName: String,

    @Column(name = "buyer_email")
    val buyerEmail: String? = null,

    @Column(name = "buyer_phone", nullable = false)
    val buyerPhone: String,

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

    @Column(name = "payment_method", nullable = false)
    val paymentMethod: String,

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 0)
    val totalAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    var orderStatus: OrderStatus = OrderStatus.PAYMENT_WAITING,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val orderItems: MutableList<OrderItem> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateStatus(status: OrderStatus) {
       this.orderStatus = status
    }
}

enum class OrderStatus {
    PAYMENT_WAITING, PAYED, DELIVERING, DELIVERED,
    PURCHASE_DECIDED, EXCHANGED, CANCELED, RETURNED
}

@Entity
@Table(name = "order_items")
class OrderItem(
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
    var status: OrderStatus = OrderStatus.PAYMENT_WAITING,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateStatus(orderStatus: OrderStatus) {
        this.status = orderStatus
    }
}