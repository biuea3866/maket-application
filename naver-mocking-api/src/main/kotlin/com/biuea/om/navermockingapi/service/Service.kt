package com.biuea.om.navermockingapi.service

import com.biuea.om.navermockingapi.api.CategoryResponse
import com.biuea.om.navermockingapi.api.OrderResponse
import com.biuea.om.navermockingapi.api.OrderSearchRequest
import com.biuea.om.navermockingapi.api.OrderStatusUpdateRequest
import com.biuea.om.navermockingapi.api.PageResponse
import com.biuea.om.navermockingapi.api.ProductCreateRequest
import com.biuea.om.navermockingapi.api.ProductListResponse
import com.biuea.om.navermockingapi.api.ProductResponse
import com.biuea.om.navermockingapi.api.ProductSearchRequest
import com.biuea.om.navermockingapi.api.ProductUpdateRequest
import com.biuea.om.navermockingapi.api.SellerRegistrationRequest
import com.biuea.om.navermockingapi.api.SellerResponse
import com.biuea.om.navermockingapi.common.BusinessException
import com.biuea.om.navermockingapi.common.generateApiKey
import com.biuea.om.navermockingapi.common.generateId
import com.biuea.om.navermockingapi.repository.CategoryRepository
import com.biuea.om.navermockingapi.repository.OrderItemRepository
import com.biuea.om.navermockingapi.repository.OrderRepository
import com.biuea.om.navermockingapi.repository.ProductImageRepository
import com.biuea.om.navermockingapi.repository.ProductOptionRepository
import com.biuea.om.navermockingapi.repository.ProductRepository
import com.biuea.om.navermockingapi.repository.SellerRepository
import com.biuea.om.navermockingapi.repository.entity.OrderStatus
import com.biuea.om.navermockingapi.repository.entity.Product
import com.biuea.om.navermockingapi.repository.entity.ProductImage
import com.biuea.om.navermockingapi.repository.entity.ProductOption
import com.biuea.om.navermockingapi.repository.entity.ProductStatus
import com.biuea.om.navermockingapi.repository.entity.Seller
import com.biuea.om.navermockingapi.repository.entity.SellerStatus
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional
class SellerService(
    private val sellerRepository: SellerRepository
) {
    fun registerSeller(request: SellerRegistrationRequest): SellerResponse {
        // 사업자번호 중복 확인
        if (sellerRepository.existsByBusinessNumber(request.businessNumber)) {
            throw BusinessException("이미 등록된 사업자번호입니다.")
        }

        val seller = Seller(
            sellerId = generateId("SELLER"),
            businessName = request.businessName,
            businessNumber = request.businessNumber,
            representativeName = request.representativeName,
            email = request.email,
            phone = request.phone,
            apiKey = generateApiKey(),
            apiSecret = generateApiKey(),
            status = SellerStatus.PENDING
        )

        val savedSeller = sellerRepository.save(seller)
        return SellerResponse.from(savedSeller)
    }

    fun getSellerBySellerId(sellerId: String): SellerResponse {
        val seller = sellerRepository.findBySellerId(sellerId)
            ?: throw BusinessException("판매자를 찾을 수 없습니다.")
        return SellerResponse.from(seller)
    }

    fun approveSeller(sellerId: String): SellerResponse {
        val seller = sellerRepository.findBySellerId(sellerId)
            ?: throw BusinessException("판매자를 찾을 수 없습니다.")

        seller.approve()

        return SellerResponse.from(seller)
    }

    fun validateApiKey(apiKey: String): Seller {
        return sellerRepository.findByApiKey(apiKey)
            ?: throw BusinessException("유효하지 않은 API 키입니다.")
    }
}

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository,
    private val productImageRepository: ProductImageRepository,
    private val productOptionRepository: ProductOptionRepository
) {
    fun createProduct(sellerId: String, request: ProductCreateRequest): ProductResponse {
        val seller = sellerRepository.findBySellerId(sellerId)
            ?: throw BusinessException("판매자를 찾을 수 없습니다.")

        val category = categoryRepository.findByCategoryId(request.categoryId)
            ?: throw BusinessException("카테고리를 찾을 수 없습니다.")

        val product = Product(
            productId = generateId("PROD"),
            seller = seller,
            name = request.name,
            category = category,
            price = request.price,
            salePrice = request.salePrice,
            stockQuantity = request.stockQuantity,
            description = request.description,
            detailContent = request.detailContent,
            mainImage = request.mainImage
        )

        val savedProduct = productRepository.save(product)

        // 이미지 저장
        request.images.forEach { imageReq ->
            val image = ProductImage(
                product = savedProduct,
                imageUrl = imageReq.imageUrl,
                imageOrder = imageReq.imageOrder
            )
            productImageRepository.save(image)
            savedProduct.images.add(image)
        }

        // 옵션 저장
        request.options.forEach { optionReq ->
            val option = ProductOption(
                optionId = generateId("OPT"),
                product = savedProduct,
                optionName = optionReq.optionName,
                optionValue = optionReq.optionValue,
                additionalPrice = optionReq.additionalPrice,
                stockQuantity = optionReq.stockQuantity
            )
            productOptionRepository.save(option)
            savedProduct.options.add(option)
        }

        return ProductResponse.from(savedProduct)
    }

    fun updateProduct(sellerId: String, productId: String, request: ProductUpdateRequest): ProductResponse {
        val product = productRepository.findByProductId(productId)
            ?: throw BusinessException("상품을 찾을 수 없습니다.")

        if (product.seller.sellerId != sellerId) {
            throw BusinessException("해당 상품을 수정할 권한이 없습니다.")
        }

        product.update(request, request.categoryId?.let { categoryRepository.findByCategoryId(it) })

        return ProductResponse.from(product)
    }

    fun getProduct(productId: String): ProductResponse {
        val product = productRepository.findByProductId(productId)
            ?: throw BusinessException("상품을 찾을 수 없습니다.")
        return ProductResponse.from(product)
    }

    fun getProductsBySeller(
        sellerId: String,
        request: ProductSearchRequest
    ): PageResponse<ProductListResponse> {
        val pageable = PageRequest.of(
            request.page - 1,
            request.size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val productPage = when {
            request.keyword != null -> {
                productRepository.searchBySellerIdAndName(sellerId, request.keyword, pageable)
            }
            request.status != null -> {
                val status = ProductStatus.valueOf(request.status)
                productRepository.findBySeller_SellerIdAndStatus(sellerId, status, pageable)
            }
            else -> {
                productRepository.findBySeller_SellerId(sellerId, pageable)
            }
        }

        return PageResponse(
            items = productPage.content.map { ProductListResponse.from(it) },
            totalItems = productPage.totalElements,
            totalPages = productPage.totalPages,
            currentPage = request.page,
            itemsPerPage = request.size
        )
    }

    fun deleteProduct(sellerId: String, productId: String) {
        val product = productRepository.findByProductId(productId)
            ?: throw BusinessException("상품을 찾을 수 없습니다.")

        if (product.seller.sellerId != sellerId) {
            throw BusinessException("해당 상품을 삭제할 권한이 없습니다.")
        }

        productRepository.delete(product)
    }
}

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository
) {
    fun getOrdersBySeller(sellerId: String, request: OrderSearchRequest): PageResponse<OrderResponse> {
        val pageable = PageRequest.of(
            request.page - 1,
            request.size,
            Sort.by(Sort.Direction.DESC, "orderDate")
        )

        val orderPage = when {
            request.startDate != null && request.endDate != null -> {
                val startDateTime = LocalDateTime.parse(request.startDate + "T00:00:00")
                val endDateTime = LocalDateTime.parse(request.endDate + "T23:59:59")
                orderRepository.findBySellerIdAndOrderDateBetween(
                    sellerId, startDateTime, endDateTime, pageable
                )
            }
            request.orderStatus != null -> {
                val status = OrderStatus.valueOf(request.orderStatus)
                orderRepository.findBySeller_SellerIdAndOrderStatus(sellerId, status, pageable)
            }
            else -> {
                orderRepository.findBySeller_SellerId(sellerId, pageable)
            }
        }

        return PageResponse(
            items = orderPage.content.map { OrderResponse.from(it) },
            totalItems = orderPage.totalElements,
            totalPages = orderPage.totalPages,
            currentPage = request.page,
            itemsPerPage = request.size
        )
    }

    fun getOrder(sellerId: String, orderId: String): OrderResponse {
        val order = orderRepository.findByOrderId(orderId)
            ?: throw BusinessException("주문을 찾을 수 없습니다.")

        if (order.seller.sellerId != sellerId) {
            throw BusinessException("해당 주문을 조회할 권한이 없습니다.")
        }

        return OrderResponse.from(order)
    }

    fun updateOrderStatus(
        sellerId: String,
        orderId: String,
        request: OrderStatusUpdateRequest
    ): OrderResponse {
        val order = orderRepository.findByOrderId(orderId)
            ?: throw BusinessException("주문을 찾을 수 없습니다.")

        if (order.seller.sellerId != sellerId) {
            throw BusinessException("해당 주문을 수정할 권한이 없습니다.")
        }

        val newStatus = try {
            OrderStatus.valueOf(request.status)
        } catch (e: IllegalArgumentException) {
            throw BusinessException("유효하지 않은 주문 상태입니다.")
        }

        order.updateStatus(newStatus)

        // 주문 아이템 상태도 함께 업데이트
        order.orderItems.forEach { item ->
            item.updateStatus(newStatus)
        }

        return OrderResponse.from(order)
    }
}

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    fun getAllCategories(): List<CategoryResponse> {
        val topCategories = categoryRepository.findByParentIdIsNull()
        return topCategories.map { category ->
            val children = categoryRepository.findByParentId(category.categoryId)
            CategoryResponse.from(
                category,
                children.map { child ->
                    val grandChildren = categoryRepository.findByParentId(child.categoryId)
                    CategoryResponse.from(child, grandChildren.map { CategoryResponse.from(it) })
                }
            )
        }
    }

    fun getActiveCategories(): List<CategoryResponse> {
        val categories = categoryRepository.findByIsActiveTrue()
        return categories.map { CategoryResponse.from(it) }
    }
}