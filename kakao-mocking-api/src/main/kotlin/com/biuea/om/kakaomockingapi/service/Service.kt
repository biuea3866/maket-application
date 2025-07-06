package com.biuea.om.kakaomockingapi.service

import com.biuea.om.kakaomockingapi.api.*
import com.biuea.om.kakaomockingapi.common.BusinessException
import com.biuea.om.kakaomockingapi.common.generateApiKey
import com.biuea.om.kakaomockingapi.common.generateId
import com.biuea.om.kakaomockingapi.repository.*
import com.biuea.om.kakaomockingapi.repository.entity.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

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

        // 카카오 계정 중복 확인
        request.kakaoAccountId?.let {
            if (sellerRepository.existsByKakaoAccountId(it)) {
                throw BusinessException("이미 등록된 카카오 계정입니다.")
            }
        }

        val seller = Seller(
            sellerId = generateId("KK_SEL"),
            kakaoAccountId = request.kakaoAccountId,
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

    fun getSellerByKakaoAccountId(kakaoAccountId: String): SellerResponse? {
        return sellerRepository.findByKakaoAccountId(kakaoAccountId)?.let {
            SellerResponse.from(it)
        }
    }

    fun approveSeller(sellerId: String): SellerResponse {
        val seller = sellerRepository.findBySellerId(sellerId)
            ?: throw BusinessException("판매자를 찾을 수 없습니다.")

        val updatedSeller = seller.copy(status = SellerStatus.APPROVED)
        sellerRepository.save(updatedSeller)

        return SellerResponse.from(updatedSeller)
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
    private val productOptionRepository: ProductOptionRepository,
    private val reviewRepository: ReviewRepository,
    private val wishlistRepository: WishlistRepository
) {
    fun createProduct(sellerId: String, request: ProductCreateRequest): ProductResponse {
        val seller = sellerRepository.findBySellerId(sellerId)
            ?: throw BusinessException("판매자를 찾을 수 없습니다.")

        val category = categoryRepository.findByCategoryId(request.categoryId)
            ?: throw BusinessException("카테고리를 찾을 수 없습니다.")

        val shippingType = try {
            ShippingType.valueOf(request.shippingType)
        } catch (e: IllegalArgumentException) {
            throw BusinessException("유효하지 않은 배송 타입입니다.")
        }

        val product = Product(
            productId = generateId("KK_PRD"),
            seller = seller,
            name = request.name,
            category = category,
            brand = request.brand,
            price = request.price,
            salePrice = request.salePrice,
            stockQuantity = request.stockQuantity,
            description = request.description,
            detailContent = request.detailContent,
            mainImage = request.mainImage,
            isKakaoPay = request.isKakaoPay,
            shippingType = shippingType,
            shippingFee = request.shippingFee ?: BigDecimal("3000")
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
                optionId = generateId("KK_OPT"),
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

        var updatedProduct = product

        request.name?.let { updatedProduct = updatedProduct.copy(name = it) }
        request.brand?.let { updatedProduct = updatedProduct.copy(brand = it) }
        request.price?.let { updatedProduct = updatedProduct.copy(price = it) }
        request.salePrice?.let { updatedProduct = updatedProduct.copy(salePrice = it) }
        request.stockQuantity?.let { updatedProduct = updatedProduct.copy(stockQuantity = it) }
        request.description?.let { updatedProduct = updatedProduct.copy(description = it) }
        request.detailContent?.let { updatedProduct = updatedProduct.copy(detailContent = it) }
        request.mainImage?.let { updatedProduct = updatedProduct.copy(mainImage = it) }
        request.isKakaoPay?.let { updatedProduct = updatedProduct.copy(isKakaoPay = it) }
        request.shippingFee?.let { updatedProduct = updatedProduct.copy(shippingFee = it) }

        request.status?.let {
            updatedProduct = updatedProduct.copy(status = ProductStatus.valueOf(it))
        }

        request.categoryId?.let { categoryId ->
            val category = categoryRepository.findByCategoryId(categoryId)
                ?: throw BusinessException("카테고리를 찾을 수 없습니다.")
            updatedProduct = updatedProduct.copy(category = category)
        }

        request.shippingType?.let {
            val shippingType = try {
                ShippingType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                throw BusinessException("유효하지 않은 배송 타입입니다.")
            }
            updatedProduct = updatedProduct.copy(shippingType = shippingType)
        }

        val savedProduct = productRepository.save(updatedProduct)
        val reviewCount = reviewRepository.countByProduct_ProductId(productId)
        val averageRating = reviewRepository.getAverageRatingByProductId(productId)
        val wishCount = wishlistRepository.countByProduct_ProductId(productId)

        return ProductResponse.from(savedProduct, reviewCount, averageRating, wishCount)
    }

    fun getProduct(productId: String): ProductResponse {
        val product = productRepository.findByProductId(productId)
            ?: throw BusinessException("상품을 찾을 수 없습니다.")

        val reviewCount = reviewRepository.countByProduct_ProductId(productId)
        val averageRating = reviewRepository.getAverageRatingByProductId(productId)
        val wishCount = wishlistRepository.countByProduct_ProductId(productId)

        return ProductResponse.from(product, reviewCount, averageRating, wishCount)
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

        val products = productPage.content.map { product ->
            val reviewCount = reviewRepository.countByProduct_ProductId(product.productId)
            val averageRating = reviewRepository.getAverageRatingByProductId(product.productId)
            ProductListResponse.from(product, reviewCount, averageRating)
        }

        return PageResponse(
            items = products,
            totalItems = productPage.totalElements,
            totalPages = productPage.totalPages,
            currentPage = request.page,
            itemsPerPage = request.size
        )
    }

    fun getKakaoPayProducts(page: Int, size: Int): PageResponse<ProductListResponse> {
        val pageable = PageRequest.of(page - 1, size)
        val productPage = productRepository.findKakaoPayProducts(pageable)

        val products = productPage.content.map { product ->
            val reviewCount = reviewRepository.countByProduct_ProductId(product.productId)
            val averageRating = reviewRepository.getAverageRatingByProductId(product.productId)
            ProductListResponse.from(product, reviewCount, averageRating)
        }

        return PageResponse(
            items = products,
            totalItems = productPage.totalElements,
            totalPages = productPage.totalPages,
            currentPage = page,
            itemsPerPage = size
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
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val productOptionRepository: ProductOptionRepository
) {
    fun addToCart(kakaoId: String, request: CartAddRequest): CartResponse {
        val product = productRepository.findByProductId(request.productId)
            ?: throw BusinessException("상품을 찾을 수 없습니다.")

        val option = request.optionId?.let {
            productOptionRepository.findByOptionId(it)
                ?: throw BusinessException("옵션을 찾을 수 없습니다.")
        }

        // 이미 장바구니에 있는지 확인
        val existingCart = cartRepository.findByBuyerKakaoIdAndProduct_ProductId(kakaoId, request.productId)

        val cart = if (existingCart != null) {
            // 수량 증가
            val updatedCart = existingCart.copy(
                quantity = existingCart.quantity + request.quantity
            )
            cartRepository.save(updatedCart)
        } else {
            // 새로 추가
            val newCart = Cart(
                cartId = generateId("KK_CART"),
                buyerKakaoId = kakaoId,
                product = product,
                option = option,
                quantity = request.quantity,
                isSelected = true
            )
            cartRepository.save(newCart)
        }

        return CartResponse.from(cart)
    }

    fun updateCart(kakaoId: String, cartId: String, request: CartUpdateRequest): CartResponse {
        val cart = cartRepository.findByCartId(cartId)
            ?: throw BusinessException("장바구니 항목을 찾을 수 없습니다.")

        if (cart.buyerKakaoId != kakaoId) {
            throw BusinessException("장바구니 수정 권한이 없습니다.")
        }

        var updatedCart = cart
        request.quantity?.let { updatedCart = updatedCart.copy(quantity = it) }
        request.isSelected?.let { updatedCart = updatedCart.copy(isSelected = it) }

        val savedCart = cartRepository.save(updatedCart)
        return CartResponse.from(savedCart)
    }

    fun getCartItems(kakaoId: String): List<CartResponse> {
        val carts = cartRepository.findByBuyerKakaoId(kakaoId)
        return carts.map { CartResponse.from(it) }
    }

    fun removeFromCart(kakaoId: String, cartId: String) {
        val cart = cartRepository.findByCartId(cartId)
            ?: throw BusinessException("장바구니 항목을 찾을 수 없습니다.")

        if (cart.buyerKakaoId != kakaoId) {
            throw BusinessException("장바구니 삭제 권한이 없습니다.")
        }

        cartRepository.delete(cart)
    }

    fun clearCart(kakaoId: String) {
        val carts = cartRepository.findByBuyerKakaoId(kakaoId)
        cartRepository.deleteAll(carts)
    }
}

@Service
@Transactional
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) {
    fun toggleWishlist(kakaoId: String, productId: String): Boolean {
        val product = productRepository.findByProductId(productId)
            ?: throw BusinessException("상품을 찾을 수 없습니다.")

        val existingWish = wishlistRepository.findByBuyerKakaoIdAndProduct_ProductId(kakaoId, productId)

        return if (existingWish != null) {
            wishlistRepository.delete(existingWish)
            false // 찜 해제
        } else {
            val wishlist = Wishlist(
                buyerKakaoId = kakaoId,
                product = product
            )
            wishlistRepository.save(wishlist)
            true // 찜 추가
        }
    }

    fun getWishlist(kakaoId: String): List<WishlistResponse> {
        val wishlists = wishlistRepository.findByBuyerKakaoId(kakaoId)

        return wishlists.map { wishlist ->
            val reviewCount = reviewRepository.countByProduct_ProductId(wishlist.product.productId)
            val averageRating = reviewRepository.getAverageRatingByProductId(wishlist.product.productId)
            WishlistResponse.from(wishlist, reviewCount, averageRating)
        }
    }

    fun isWished(kakaoId: String, productId: String): Boolean {
        return wishlistRepository.existsByBuyerKakaoIdAndProduct_ProductId(kakaoId, productId)
    }
}

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val cartRepository: CartRepository,
    private val notificationService: KakaoNotificationService
) {
    fun createOrder(kakaoId: String?, request: OrderCreateRequest): OrderResponse {
        // 주문 상품 검증 및 총액 계산
        var totalAmount = BigDecimal.ZERO
        val orderItems = mutableListOf<OrderItem>()

        val order = Order(
            orderId = generateId("KK_ORD"),
            seller = null!!, // 임시 - 아래에서 설정
            kakaoOrderId = request.kakaoOrderId,
            orderDate = LocalDateTime.now(),
            buyerName = request.buyerName,
            buyerEmail = request.buyerEmail,
            buyerPhone = request.buyerPhone,
            buyerKakaoId = kakaoId,
            receiverName = request.receiverName,
            receiverPhone = request.receiverPhone,
            receiverZipcode = request.receiverZipcode,
            receiverAddress = request.receiverAddress,
            receiverAddressDetail = request.receiverAddressDetail,
            paymentMethod = PaymentMethod.valueOf(request.paymentMethod),
            totalAmount = totalAmount,
            deliveryMessage = request.deliveryMessage,
            orderStatus = OrderStatus.PAYMENT_WAITING
        )

        var sellerId: String? = null

        request.items.forEach { item ->
            val product = productRepository.findByProductId(item.productId)
                ?: throw BusinessException("상품을 찾을 수 없습니다: ${item.productId}")

            // 모든 상품이 같은 판매자여야 함
            if (sellerId == null) {
                sellerId = product.seller.sellerId
            } else if (sellerId != product.seller.sellerId) {
                throw BusinessException("여러 판매자의 상품을 한 번에 주문할 수 없습니다.")
            }

            val option = item.optionId?.let {
                productOptionRepository.findByOptionId(it)
                    ?: throw BusinessException("옵션을 찾을 수 없습니다: $it")
            }

            // 재고 확인
            val requiredStock = item.quantity
            val availableStock = option?.stockQuantity ?: product.stockQuantity

            if (availableStock < requiredStock) {
                throw BusinessException("재고가 부족합니다: ${product.name}")
            }

            // 가격 계산
            val productPrice = product.salePrice ?: product.price
            val optionPrice = option?.additionalPrice ?: BigDecimal.ZERO
            val itemPrice = productPrice.add(optionPrice)
            val itemTotalPrice = itemPrice.multiply(BigDecimal(item.quantity))

            totalAmount = totalAmount.add(itemTotalPrice)

            val orderItem = OrderItem(
                order = order,
                product = product,
                option = option,
                quantity = item.quantity,
                price = itemPrice,
                status = OrderStatus.PAYMENT_WAITING
            )
            orderItems.add(orderItem)
        }

        // 판매자 설정
        val seller = productRepository.findByProductId(request.items.first().productId)!!.seller
        val finalOrder = order.copy(
            seller = seller,
            totalAmount = totalAmount
        )

        val savedOrder = orderRepository.save(finalOrder)

        // 주문 아이템 저장
        orderItems.forEach { item ->
            val savedItem = item.copy(order = savedOrder)
            orderItemRepository.save(savedItem)
            savedOrder.orderItems.add(savedItem)
        }

        // 장바구니에서 제거 (카카오 ID가 있는 경우)
        kakaoId?.let {
            request.items.forEach { item ->
                cartRepository.deleteByBuyerKakaoIdAndProduct_ProductId(it, item.productId)
            }
        }

        // 카카오톡 알림 발송
        notificationService.sendOrderNotification(savedOrder)

        return OrderResponse.from(savedOrder)
    }

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
            request.paymentMethod != null -> {
                val paymentMethod = PaymentMethod.valueOf(request.paymentMethod)
                orderRepository.findByPaymentMethod(paymentMethod, pageable)
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

    fun getOrdersByBuyer(kakaoId: String, page: Int, size: Int): PageResponse<OrderResponse> {
        val pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "orderDate"))
        val orderPage = orderRepository.findByBuyerKakaoIdOrderByOrderDateDesc(kakaoId, pageable)

        return PageResponse(
            items = orderPage.content.map { OrderResponse.from(it) },
            totalItems = orderPage.totalElements,
            totalPages = orderPage.totalPages,
            currentPage = page,
            itemsPerPage = size
        )
    }

    fun getOrder(orderId: String): OrderResponse {
        val order = orderRepository.findByOrderId(orderId)
            ?: throw BusinessException("주문을 찾을 수 없습니다.")
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

        val updatedOrder = order.copy(orderStatus = newStatus)
        orderRepository.save(updatedOrder)

        // 주문 아이템 상태도 함께 업데이트
        order.orderItems.forEach { item ->
            val updatedItem = item.copy(status = newStatus)
            orderItemRepository.save(updatedItem)
        }

        // 상태에 따른 알림 발송
        when (newStatus) {
            OrderStatus.DELIVERING -> notificationService.sendDeliveryStartNotification(updatedOrder)
            OrderStatus.DELIVERED -> notificationService.sendDeliveryCompleteNotification(updatedOrder)
            OrderStatus.CANCELED -> notificationService.sendOrderCancelNotification(updatedOrder)
            else -> {}
        }

        return OrderResponse.from(updatedOrder)
    }
}

@Service
@Transactional
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val reviewImageRepository: ReviewImageRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {
    fun createReview(kakaoId: String, request: ReviewCreateRequest): ReviewResponse {
        // 주문 확인
        val order = orderRepository.findByOrderId(request.orderId)
            ?: throw BusinessException("주문을 찾을 수 없습니다.")

        // 구매자 확인
        if (order.buyerKakaoId != kakaoId) {
            throw BusinessException("리뷰 작성 권한이 없습니다.")
        }

        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByOrder_OrderIdAndBuyerKakaoId(request.orderId, kakaoId)) {
            throw BusinessException("이미 리뷰를 작성하셨습니다.")
        }

        // 상품 확인
        val product = productRepository.findByProductId(request.productId)
            ?: throw BusinessException("상품을 찾을 수 없습니다.")

        // 주문한 상품인지 확인
        val orderedProduct = order.orderItems.any { it.product.productId == request.productId }
        if (!orderedProduct) {
            throw BusinessException("주문하지 않은 상품입니다.")
        }

        val review = Review(
            reviewId = generateId("KK_REV"),
            product = product,
            order = order,
            buyerKakaoId = kakaoId,
            buyerName = order.buyerName,
            rating = request.rating,
            content = request.content,
            isPhotoReview = request.imageUrls.isNotEmpty()
        )

        val savedReview = reviewRepository.save(review)

        // 리뷰 이미지 저장
        request.imageUrls.forEachIndexed { index, imageUrl ->
            val reviewImage = ReviewImage(
                review = savedReview,
                imageUrl = imageUrl,
                imageOrder = index
            )
            reviewImageRepository.save(reviewImage)
            savedReview.images.add(reviewImage)
        }

        return ReviewResponse.from(savedReview)
    }

    fun updateReview(kakaoId: String, reviewId: String, request: ReviewUpdateRequest): ReviewResponse {
        val review = reviewRepository.findByReviewId(reviewId)
            ?: throw BusinessException("리뷰를 찾을 수 없습니다.")

        if (review.buyerKakaoId != kakaoId) {
            throw BusinessException("리뷰 수정 권한이 없습니다.")
        }

        var updatedReview = review
        request.rating?.let { updatedReview = updatedReview.copy(rating = it) }
        request.content?.let { updatedReview = updatedReview.copy(content = it) }

        val savedReview = reviewRepository.save(updatedReview)
        return ReviewResponse.from(savedReview)
    }

    fun getProductReviews(productId: String, page: Int, size: Int): PageResponse<ReviewResponse> {
        val pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val reviewPage = reviewRepository.findByProduct_ProductId(productId, pageable)

        return PageResponse(
            items = reviewPage.content.map { ReviewResponse.from(it) },
            totalItems = reviewPage.totalElements,
            totalPages = reviewPage.totalPages,
            currentPage = page,
            itemsPerPage = size
        )
    }

    fun deleteReview(kakaoId: String, reviewId: String) {
        val review = reviewRepository.findByReviewId(reviewId)
            ?: throw BusinessException("리뷰를 찾을 수 없습니다.")

        if (review.buyerKakaoId != kakaoId) {
            throw BusinessException("리뷰 삭제 권한이 없습니다.")
        }

        reviewRepository.delete(review)
    }
}

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    fun getAllCategories(): List<CategoryResponse> {
        val topCategories = categoryRepository.findByParentIdIsNullOrderByDisplayOrder()
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

@Service
@Transactional
class KakaoNotificationService(
    private val notificationRepository: KakaoNotificationRepository,
    private val orderRepository: OrderRepository
) {
    fun sendOrderNotification(order: Order) {
        val notification = KakaoNotification(
            notificationId = generateId("KK_NOTIF"),
            order = order,
            receiverPhone = order.buyerPhone,
            templateCode = "ORDER_COMPLETE",
            messageContent = "주문이 완료되었습니다. 주문번호: ${order.orderId}",
            sendStatus = NotificationStatus.SUCCESS,
            sentAt = LocalDateTime.now()
        )
        notificationRepository.save(notification)
    }

    fun sendDeliveryStartNotification(order: Order) {
        val notification = KakaoNotification(
            notificationId = generateId("KK_NOTIF"),
            order = order,
            receiverPhone = order.receiverPhone,
            templateCode = "DELIVERY_START",
            messageContent = "상품 배송이 시작되었습니다. 주문번호: ${order.orderId}",
            sendStatus = NotificationStatus.SUCCESS,
            sentAt = LocalDateTime.now()
        )
        notificationRepository.save(notification)
    }

    fun sendDeliveryCompleteNotification(order: Order) {
        val notification = KakaoNotification(
            notificationId = generateId("KK_NOTIF"),
            order = order,
            receiverPhone = order.receiverPhone,
            templateCode = "DELIVERY_COMPLETE",
            messageContent = "상품이 배송 완료되었습니다. 주문번호: ${order.orderId}",
            sendStatus = NotificationStatus.SUCCESS,
            sentAt = LocalDateTime.now()
        )
        notificationRepository.save(notification)
    }

    fun sendOrderCancelNotification(order: Order) {
        val notification = KakaoNotification(
            notificationId = generateId("KK_NOTIF"),
            order = order,
            receiverPhone = order.buyerPhone,
            templateCode = "ORDER_CANCEL",
            messageContent = "주문이 취소되었습니다. 주문번호: ${order.orderId}",
            sendStatus = NotificationStatus.SUCCESS,
            sentAt = LocalDateTime.now()
        )
        notificationRepository.save(notification)
    }

    fun sendCustomNotification(request: KakaoNotificationRequest): KakaoNotificationResponse {
        val order = request.orderId?.let {
            orderRepository.findByOrderId(it)
        }

        val messageContent = buildMessageContent(request.templateCode, request.templateParams)

        val notification = KakaoNotification(
            notificationId = generateId("KK_NOTIF"),
            order = order,
            receiverPhone = request.receiverPhone,
            templateCode = request.templateCode,
            messageContent = messageContent,
            sendStatus = NotificationStatus.PENDING
        )

        val savedNotification = notificationRepository.save(notification)

        // 실제 발송 로직 (모킹이므로 성공 처리)
        val sentNotification = savedNotification.copy(
            sendStatus = NotificationStatus.SUCCESS,
            sentAt = LocalDateTime.now()
        )
        notificationRepository.save(sentNotification)

        return KakaoNotificationResponse.from(sentNotification)
    }

    private fun buildMessageContent(templateCode: String, params: Map<String, String>): String {
        // 템플릿 코드에 따른 메시지 생성 (실제로는 템플릿 시스템 사용)
        return when (templateCode) {
            "ORDER_COMPLETE" -> "주문이 완료되었습니다. 주문번호: ${params["orderId"]}"
            "DELIVERY_START" -> "배송이 시작되었습니다. 주문번호: ${params["orderId"]}"
            else -> "카카오톡 알림"
        }
    }
}