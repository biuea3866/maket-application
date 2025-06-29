package com.biuea.om.navermockingapi.api

import com.biuea.om.navermockingapi.service.CategoryService
import com.biuea.om.navermockingapi.service.OrderService
import com.biuea.om.navermockingapi.service.ProductService
import com.biuea.om.navermockingapi.service.SellerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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
        @Parameter(description = "상품 상태 (SALE, SUSPEND, OUTOFSTOCK, CLOSE)")
        @RequestParam(required = false) status: String?,
        @Parameter(description = "카테고리 ID")
        @RequestParam(required = false) categoryId: String?,
        @Parameter(description = "검색 키워드")
        @RequestParam(required = false) keyword: String?,
        @Parameter(description = "페이지 번호 (1부터 시작)")
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "페이지 크기")
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<ProductListResponse>> {
        val seller = sellerService.validateApiKey(apiKey)
        val request = ProductSearchRequest(
            status = status,
            categoryId = categoryId,
            keyword = keyword,
            page = page,
            size = size
        )
        val products = productService.getProductsBySeller(seller.sellerId, request)
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
@RequestMapping("/v1/orders")
@Tag(name = "주문 API", description = "주문 조회 및 관리 API")
class OrderController(
    private val orderService: OrderService,
    private val sellerService: SellerService
) {
    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "판매자의 주문 목록을 조회합니다")
    fun getOrders(
        @RequestHeader("X-API-KEY") apiKey: String,
        @Parameter(description = "주문 상태")
        @RequestParam(required = false) orderStatus: String?,
        @Parameter(description = "조회 시작일 (YYYY-MM-DD)")
        @RequestParam(required = false) startDate: String?,
        @Parameter(description = "조회 종료일 (YYYY-MM-DD)")
        @RequestParam(required = false) endDate: String?,
        @Parameter(description = "구매자명")
        @RequestParam(required = false) buyerName: String?,
        @Parameter(description = "페이지 번호")
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "페이지 크기")
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<OrderResponse>> {
        val seller = sellerService.validateApiKey(apiKey)
        val request = OrderSearchRequest(
            orderStatus = orderStatus,
            startDate = startDate,
            endDate = endDate,
            buyerName = buyerName,
            page = page,
            size = size
        )
        val orders = orderService.getOrdersBySeller(seller.sellerId, request)
        return ApiResponse(data = orders)
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 ID로 상세 정보를 조회합니다")
    fun getOrder(
        @RequestHeader("X-API-KEY") apiKey: String,
        @PathVariable orderId: String
    ): ApiResponse<OrderResponse> {
        val seller = sellerService.validateApiKey(apiKey)
        val order = orderService.getOrder(seller.sellerId, orderId)
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