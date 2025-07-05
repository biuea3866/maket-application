package com.biuea.om.kakaomockingapi.repository

import com.biuea.om.kakaomockingapi.repository.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByReviewId(reviewId: String): Review?
    fun findByProduct_ProductId(productId: String, pageable: Pageable): Page<Review>
    fun findByProduct_ProductIdAndRatingGreaterThanEqual(productId: String, rating: Int, pageable: Pageable): Page<Review>
    fun findByBuyerKakaoId(buyerKakaoId: String, pageable: Pageable): Page<Review>
    fun findByIsPhotoReviewTrue(pageable: Pageable): Page<Review>

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    fun getAverageRatingByProductId(productId: String): Double?

    fun countByProduct_ProductId(productId: String): Long
    fun countByProduct_ProductIdAndRating(productId: String, rating: Int): Long
    fun existsByOrder_OrderIdAndBuyerKakaoId(orderId: String, buyerKakaoId: String): Boolean
}

@Repository
interface ReviewImageRepository : JpaRepository<ReviewImage, Long> {
    fun findByReview_ReviewIdOrderByImageOrder(reviewId: String): List<ReviewImage>
    fun deleteByReview_ReviewId(reviewId: String)
}

@Repository
interface KakaoNotificationRepository : JpaRepository<KakaoNotification, Long> {
    fun findByNotificationId(notificationId: String): Page<Product>
    fun findByStatus(status: ProductStatus, pageable: Pageable): Page<Product>
    fun findBySeller_SellerIdAndStatus(sellerId: String, status: ProductStatus, pageable: Pageable): Page<Product>
    fun findByIsKakaoPayTrue(pageable: Pageable): Page<Product>
    fun findByCategory_CategoryIdIn(categoryIds: List<String>, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.seller.sellerId = :sellerId AND p.name LIKE %:keyword%")
    fun searchBySellerIdAndName(sellerId: String, keyword: String, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.isKakaoPay = true AND p.status = 'SALE' ORDER BY p.createdAt DESC")
    fun findKakaoPayProducts(pageable: Pageable): Page<Product>

    fun countBySeller_SellerId(sellerId: String): Long
}

@Repository
interface ProductImageRepository : JpaRepository<ProductImage, Long> {
    fun findByProduct_ProductIdOrderByImageOrder(productId: String): List<ProductImage>
    fun deleteByProduct_ProductId(productId: String)
}

@Repository
interface ProductOptionRepository : JpaRepository<ProductOption, Long> {
    fun findByOptionId(optionId: String): ProductOption?
    fun findByProduct_ProductIdAndUseYnTrue(productId: String): List<ProductOption>
    fun deleteByProduct_ProductId(productId: String)
}

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByOrderId(orderId: String): Order?
    fun findBySeller_SellerId(sellerId: String, pageable: Pageable): Page<Order>
    fun findBySeller_SellerIdAndOrderStatus(sellerId: String, status: OrderStatus, pageable: Pageable): Page<Order>
    fun findByBuyerKakaoId(buyerKakaoId: String, pageable: Pageable): Page<Order>
    fun findByPaymentMethod(paymentMethod: PaymentMethod, pageable: Pageable): Page<Order>

    @Query("SELECT o FROM Order o WHERE o.seller.sellerId = :sellerId AND o.orderDate BETWEEN :startDate AND :endDate")
    fun findBySellerIdAndOrderDateBetween(
        sellerId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<Order>

    @Query("SELECT o FROM Order o WHERE o.buyerKakaoId = :kakaoId ORDER BY o.orderDate DESC")
    fun findByBuyerKakaoIdOrderByOrderDateDesc(kakaoId: String, pageable: Pageable): Page<Order>

    fun countBySeller_SellerId(sellerId: String): Long
    fun countBySeller_SellerIdAndOrderStatus(sellerId: String, status: OrderStatus): Long
    fun countByPaymentMethod(paymentMethod: PaymentMethod): Long
}

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    fun findByOrder_OrderId(orderId: String): List<OrderItem>
    fun findByProduct_ProductId(productId: String): List<OrderItem>
}

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findByCartId(cartId: String): Cart?
    fun findByBuyerKakaoId(buyerKakaoId: String): List<Cart>
    fun findByBuyerKakaoIdAndIsSelectedTrue(buyerKakaoId: String): List<Cart>
    fun findByBuyerKakaoIdAndProduct_ProductId(buyerKakaoId: String, productId: String): Cart?
    fun deleteByBuyerKakaoIdAndProduct_ProductId(buyerKakaoId: String, productId: String)
    fun countByBuyerKakaoId(buyerKakaoId: String): Long
}

@Repository
interface WishlistRepository : JpaRepository<Wishlist, Long> {
    fun findByBuyerKakaoId(buyerKakaoId: String): List<Wishlist>
    fun findByBuyerKakaoIdAndProduct_ProductId(buyerKakaoId: String, productId: String): Wishlist?
    fun existsByBuyerKakaoIdAndProduct_ProductId(buyerKakaoId: String, productId: String): Boolean
    fun deleteByBuyerKakaoIdAndProduct_ProductId(buyerKakaoId: String, productId: String)
    fun countByBuyerKakaoId(buyerKakaoId: String): Long
    fun countByProduct_ProductId(productId: String): Long
}

@Repository
interface SellerRepository : JpaRepository<Seller, Long> {
    fun findBySellerId(sellerId: String): Seller?
    fun findByApiKey(apiKey: String): Seller?
    fun findByKakaoAccountId(kakaoAccountId: String): Seller?
    fun findByBusinessNumber(businessNumber: String): Seller?
    fun existsByBusinessNumber(businessNumber: String): Boolean
    fun existsBySellerId(sellerId: String): Boolean
    fun existsByKakaoAccountId(kakaoAccountId: String): Boolean
}

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategoryId(categoryId: String): Category?
    fun findByParentIdIsNull(): List<Category>
    fun findByParentId(parentId: String): List<Category>
    fun findByIsActiveTrue(): List<Category>
    fun findByParentIdIsNullOrderByDisplayOrder(): List<Category>
}

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByProductId(productId: String): Product?
    fun findBySeller_