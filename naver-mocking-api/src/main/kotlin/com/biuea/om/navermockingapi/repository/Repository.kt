package com.biuea.om.navermockingapi.repository

import com.biuea.om.navermockingapi.repository.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SellerRepository : JpaRepository<Seller, Long> {
    fun findBySellerId(sellerId: String): Seller?
    fun findByApiKey(apiKey: String): Seller?
    fun findByBusinessNumber(businessNumber: String): Seller?
    fun existsByBusinessNumber(businessNumber: String): Boolean
    fun existsBySellerId(sellerId: String): Boolean
}

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategoryId(categoryId: String): Category?
    fun findByParentIdIsNull(): List<Category>
    fun findByParentId(parentId: String): List<Category>
    fun findByIsActiveTrue(): List<Category>
}

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByProductId(productId: String): Product?
    fun findBySeller_SellerId(sellerId: String, pageable: Pageable): Page<Product>
    fun findByStatus(status: ProductStatus, pageable: Pageable): Page<Product>
    fun findBySeller_SellerIdAndStatus(sellerId: String, status: ProductStatus, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.seller.sellerId = :sellerId AND p.name LIKE %:keyword%")
    fun searchBySellerIdAndName(sellerId: String, keyword: String, pageable: Pageable): Page<Product>

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

    @Query("SELECT o FROM Order o WHERE o.seller.sellerId = :sellerId AND o.orderDate BETWEEN :startDate AND :endDate")
    fun findBySellerIdAndOrderDateBetween(
        sellerId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<Order>

    fun countBySeller_SellerId(sellerId: String): Long
    fun countBySeller_SellerIdAndOrderStatus(sellerId: String, status: OrderStatus): Long
}

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    fun findByOrder_OrderId(orderId: String): List<OrderItem>
    fun findByProduct_ProductId(productId: String): List<OrderItem>
}