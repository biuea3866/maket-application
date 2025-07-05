package com.biuea.om.kakaomockingapi.common

import com.biuea.om.kakaomockingapi.api.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class BusinessException(
    message: String,
    val code: String = "BUSINESS_ERROR"
) : RuntimeException(message)

class UnauthorizedException(
    message: String = "인증이 필요합니다",
    val code: String = "UNAUTHORIZED"
) : RuntimeException(message)

class KakaoAuthException(
    message: String = "카카오 인증 오류",
    val code: String = "KAKAO_AUTH_ERROR"
) : RuntimeException(message)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = e.code,
            message = e.message ?: "비즈니스 로직 오류가 발생했습니다"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = e.code,
            message = e.message ?: "인증이 필요합니다"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(KakaoAuthException::class)
    fun handleKakaoAuthException(e: KakaoAuthException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = e.code,
            message = e.message ?: "카카오 인증 오류가 발생했습니다"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = mutableMapOf<String, String>()
        e.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "유효하지 않은 값입니다"
            errors[fieldName] = errorMessage
        }

        val response = ErrorResponse(
            code = "VALIDATION_ERROR",
            message = "입력값 검증에 실패했습니다",
            details = errors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = "INTERNAL_ERROR",
            message = "서버 오류가 발생했습니다"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}