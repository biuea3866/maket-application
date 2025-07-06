package com.biuea.om.kakaomockingapi.api

import com.biuea.om.kakaomockingapi.service.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("/v1/seller")
@Tag(name = "판매자 API", description = "판매자 입점 및 관리 API")
class SellerController(
    private val sellerService: SellerService
) {
    @PostMapping("/register")
    @Operation(summary = "판매자 입점 신청", description = "새로운 판매자 입점을 신청합니다")
    fun registerSeller(
        @Valid @RequestBody request: SellerRegistrationRequest
    ): ApiResponse<SellerResponse> {
        val seller = sellerService.registerSeller(request)
        return ApiResponse(data = seller, message = "입점 신청이 완료되었습니다")
    }

    @GetMapping("/{sellerId}")
    @Operation(summary = "판매자 정보 조회", description = "판매자 ID로 정보를 조회합니다")
    fun getSeller(
        @PathVariable sellerId: String
    ): ApiResponse<SellerResponse> {
        val seller = sellerService.getSellerBySellerId(sellerId)
        return ApiResponse(data = seller)
    }

    @GetMapping("/kakao/{kakaoAccountId}")
    @Operation(summary = "카카오 계정으로 판매자 조회", description = "카카오 계정 ID로 판매자 정보를 조회합니다")
    fun getSellerByKakaoAccount(
        @PathVariable kakaoAccountId: String
    ): ApiResponse<SellerResponse?> {
        val seller = sellerService.getSellerByKakaoAccountId(kakaoAccountId)
        return ApiResponse(data = seller)
    }

    @PutMapping("/{sellerId}/approve")
    @Operation(summary = "판매자 승인", description = "입점 신청한 판매자를 승인합니다")
    fun approveSeller(
        @PathVariable sellerId: String
    ): ApiResponse<SellerResponse> {
        val seller = sellerService.approveSeller(sellerId)
        return ApiResponse(data = seller, message = "판매자가 승인되었습니다")
    }
}

@RestController
@RequestMapping("/v1/products")
@Tag(name = "상품 API", description = "상품 등록, 수정, 조회 API")
class ProductController(
    private val productService: ProductService,
    private val sellerService: SellerService
) {
    @PostMapping
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다")
    fun createProduct(
        @RequestHeader("X-API-KEY") apiKey: String,
        @Valid @RequestBody request: ProductCreateRequest
    ): ApiResponse<ProductResponse> {
        val seller = sellerService.validateApiKey(apiKey)
        val product = productService.createProduct(seller.sellerId, request)
        return ApiResponse(data = product, message = "상품이 등록되었습니다")
    }

    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다")
    fun updateProduct(
        @RequestHeader("X-API-KEY") apiKey: String,
        @PathVariable productId: String,
        @RequestBody request: ProductUpdateRequest
    ): ApiResponse<ProductResponse> {
        val seller = sellerService.validateApiKey(apiKey)
        val product = productService.updateProduct(seller.sellerId, productId, request)
        return ApiResponse(data = product, message = "상품이 수정되었습니다")
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다")
    fun getProduct(
        @PathVariable productId: String
    ): ApiResponse<ProductResponse> {
        val product = productService.getProduct(productId)
        return ApiResponse(data = product)
    }

    @GetMapping
    @Operation(summary = "판매자 상품 목록 조회", description = "판매자의 상품 목록을 조회합니다")
    fun getProductsBySeller(
        @RequestHeader("X-API-KEY") apiKey: String,
        @Parameter(description = "상품 상태") @RequestParam(required = false) status: String?,
        @Parameter(description = "카테고리 ID") @RequestParam(required = false) categoryId: String?,
        @Parameter(description = "검색 키워드") @RequestParam(required = false) keyword: String?,
        @Parameter(description = "카카오페이 가능 여부") @RequestParam(required = false) isKakaoPay: Boolean?,
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<ProductListResponse>> {
        val seller = sellerService.validateApiKey(apiKey)
        val request = ProductSearchRequest(
            status = status,
            categoryId = categoryId,
            keyword = keyword,
            isKakaoPay = isKakaoPay,
            page = page,
            size = size
        )
        val products = productService.getProductsBySeller(seller.sellerId, request)
        return ApiResponse(data = products)
    }

    @GetMapping("/kakao-pay")
    @Operation(summary = "카카오페이 상품 목록", description = "카카오페이로 결제 가능한 상품 목록을 조회합니다")
    fun getKakaoPayProducts(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<ProductListResponse>> {
        val products = productService.getKakaoPayProducts(page, size)
        return ApiResponse(data = products)
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다")
    fun deleteProduct(
        @RequestHeader("X-API-KEY") apiKey: String,
        @PathVariable productId: String
    ) {
        val seller = sellerService.validateApiKey(apiKey)
        productService.deleteProduct(seller.sellerId, productId)
    }
}

@RestController
@RequestMapping("/v1/cart")
@Tag(name = "장바구니 API", description = "장바구니 관리 API")
class CartController(
    private val cartService: CartService
) {
    @PostMapping
    @Operation(summary = "장바구니 추가", description = "상품을 장바구니에 추가합니다")
    fun addToCart(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @Valid @RequestBody request: CartAddRequest
    ): ApiResponse<CartResponse> {
        val cart = cartService.addToCart(kakaoId, request)
        return ApiResponse(data = cart, message = "장바구니에 추가되었습니다")
    }

    @PutMapping("/{cartId}")
    @Operation(summary = "장바구니 수정", description = "장바구니 항목을 수정합니다")
    fun updateCart(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @PathVariable cartId: String,
        @RequestBody request: CartUpdateRequest
    ): ApiResponse<CartResponse> {
        val cart = cartService.updateCart(kakaoId, cartId, request)
        return ApiResponse(data = cart, message = "장바구니가 수정되었습니다")
    }

    @GetMapping
    @Operation(summary = "장바구니 조회", description = "장바구니 목록을 조회합니다")
    fun getCartItems(
        @RequestHeader("X-KAKAO-ID") kakaoId: String
    ): ApiResponse<List<CartResponse>> {
        val items = cartService.getCartItems(kakaoId)
        return ApiResponse(data = items)
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 항목을 삭제합니다")
    fun removeFromCart(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @PathVariable cartId: String
    ) {
        cartService.removeFromCart(kakaoId, cartId)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "장바구니 비우기", description = "장바구니를 모두 비웁니다")
    fun clearCart(
        @RequestHeader("X-KAKAO-ID") kakaoId: String
    ) {
        cartService.clearCart(kakaoId)
    }
}

@RestController
@RequestMapping("/v1/wishlist")
@Tag(name = "찜 API", description = "찜 목록 관리 API")
class WishlistController(
    private val wishlistService: WishlistService
) {
    @PostMapping("/toggle")
    @Operation(summary = "찜 토글", description = "상품을 찜 목록에 추가하거나 제거합니다")
    fun toggleWishlist(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @Valid @RequestBody request: WishlistRequest
    ): ApiResponse<Map<String, Boolean>> {
        val isWished = wishlistService.toggleWishlist(kakaoId, request.productId)
        return ApiResponse(
            data = mapOf("isWished" to isWished),
            message = if (isWished) "찜 목록에 추가되었습니다" else "찜 목록에서 제거되었습니다"
        )
    }

    @GetMapping
    @Operation(summary = "찜 목록 조회", description = "찜한 상품 목록을 조회합니다")
    fun getWishlist(
        @RequestHeader("X-KAKAO-ID") kakaoId: String
    ): ApiResponse<List<WishlistResponse>> {
        val wishlist = wishlistService.getWishlist(kakaoId)
        return ApiResponse(data = wishlist)
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "찜 여부 확인", description = "특정 상품의 찜 여부를 확인합니다")
    fun checkWishlist(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @PathVariable productId: String
    ): ApiResponse<Map<String, Boolean>> {
        val isWished = wishlistService.isWished(kakaoId, productId)
        return ApiResponse(data = mapOf("isWished" to isWished))
    }
}

@RestController
@RequestMapping("/v1/orders")
@Tag(name = "주문 API", description = "주문 생성 및 관리 API")
class OrderController(
    private val orderService: OrderService,
    private val sellerService: SellerService
) {
    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다")
    fun createOrder(
        @RequestHeader("X-KAKAO-ID", required = false) kakaoId: String?,
        @Valid @RequestBody request: OrderCreateRequest
    ): ApiResponse<OrderResponse> {
        val order = orderService.createOrder(kakaoId, request)
        return ApiResponse(data = order, message = "주문이 완료되었습니다")
    }

    @GetMapping("/seller")
    @Operation(summary = "판매자 주문 목록", description = "판매자의 주문 목록을 조회합니다")
    fun getOrdersBySeller(
        @RequestHeader("X-API-KEY") apiKey: String,
        @Parameter(description = "주문 상태") @RequestParam(required = false) orderStatus: String?,
        @Parameter(description = "조회 시작일") @RequestParam(required = false) startDate: String?,
        @Parameter(description = "조회 종료일") @RequestParam(required = false) endDate: String?,
        @Parameter(description = "결제 수단") @RequestParam(required = false) paymentMethod: String?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<OrderResponse>> {
        val seller = sellerService.validateApiKey(apiKey)
        val request = OrderSearchRequest(
            orderStatus = orderStatus,
            startDate = startDate,
            endDate = endDate,
            paymentMethod = paymentMethod,
            page = page,
            size = size
        )
        val orders = orderService.getOrdersBySeller(seller.sellerId, request)
        return ApiResponse(data = orders)
    }

    @GetMapping("/buyer")
    @Operation(summary = "구매자 주문 목록", description = "구매자의 주문 목록을 조회합니다")
    fun getOrdersByBuyer(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<OrderResponse>> {
        val orders = orderService.getOrdersByBuyer(kakaoId, page, size)
        return ApiResponse(data = orders)
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 ID로 상세 정보를 조회합니다")
    fun getOrder(
        @PathVariable orderId: String
    ): ApiResponse<OrderResponse> {
        val order = orderService.getOrder(orderId)
        return ApiResponse(data = order)
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다")
    fun updateOrderStatus(
        @RequestHeader("X-API-KEY") apiKey: String,
        @PathVariable orderId: String,
        @Valid @RequestBody request: OrderStatusUpdateRequest
    ): ApiResponse<OrderResponse> {
        val seller = sellerService.validateApiKey(apiKey)
        val order = orderService.updateOrderStatus(seller.sellerId, orderId, request)
        return ApiResponse(data = order, message = "주문 상태가 변경되었습니다")
    }
}

@RestController
@RequestMapping("/v1/reviews")
@Tag(name = "리뷰 API", description = "상품 리뷰 관리 API")
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping
    @Operation(summary = "리뷰 작성", description = "상품 리뷰를 작성합니다")
    fun createReview(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @Valid @RequestBody request: ReviewCreateRequest
    ): ApiResponse<ReviewResponse> {
        val review = reviewService.createReview(kakaoId, request)
        return ApiResponse(data = review, message = "리뷰가 작성되었습니다")
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다")
    fun updateReview(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @PathVariable reviewId: String,
        @RequestBody request: ReviewUpdateRequest
    ): ApiResponse<ReviewResponse> {
        val review = reviewService.updateReview(kakaoId, reviewId, request)
        return ApiResponse(data = review, message = "리뷰가 수정되었습니다")
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "상품 리뷰 조회", description = "특정 상품의 리뷰 목록을 조회합니다")
    fun getProductReviews(
        @PathVariable productId: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<ReviewResponse>> {
        val reviews = reviewService.getProductReviews(productId, page, size)
        return ApiResponse(data = reviews)
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다")
    fun deleteReview(
        @RequestHeader("X-KAKAO-ID") kakaoId: String,
        @PathVariable reviewId: String
    ) {
        reviewService.deleteReview(kakaoId, reviewId)
    }
}

@RestController
@RequestMapping("/v1/categories")
@Tag(name = "카테고리 API", description = "상품 카테고리 조회 API")
class CategoryController(
    private val categoryService: CategoryService
) {
    @GetMapping
    @Operation(summary = "전체 카테고리 조회", description = "계층 구조의 전체 카테고리를 조회합니다")
    fun getAllCategories(): ApiResponse<List<CategoryResponse>> {
        val categories = categoryService.getAllCategories()
        return ApiResponse(data = categories)
    }

    @GetMapping("/active")
    @Operation(summary = "활성 카테고리 조회", description = "활성화된 카테고리만 조회합니다")
    fun getActiveCategories(): ApiResponse<List<CategoryResponse>> {
        val categories = categoryService.getActiveCategories()
        return ApiResponse(data = categories)
    }
}

@RestController
@RequestMapping("/v1/notifications")
@Tag(name = "카카오톡 알림 API", description = "카카오톡 알림톡 발송 API")
class KakaoNotificationController(
    private val notificationService: KakaoNotificationService
) {
    @PostMapping("/send")
    @Operation(summary = "알림톡 발송", description = "카카오톡 알림톡을 발송합니다")
    fun sendNotification(
        @Valid @RequestBody request: KakaoNotificationRequest
    ): ApiResponse<KakaoNotificationResponse> {
        val notification = notificationService.sendCustomNotification(request)
        return ApiResponse(data = notification, message = "알림톡이 발송되었습니다")
    }
}