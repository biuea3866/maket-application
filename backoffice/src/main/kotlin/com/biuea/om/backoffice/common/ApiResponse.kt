package com.biuea.om.backoffice.common

data class ApiResponse<T>(
    val code: String,
    val data: T?
) {
    companion object {
        fun<T> success(data: T?): ApiResponse<T> {
            return ApiResponse(
                data = data,
                code = "ok"
            )
        }
    }
}