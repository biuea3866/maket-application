package com.biuea.om.kakaomockingapi.common

import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ID 생성 함수
fun generateId(prefix: String): String {
    val timestamp = System.currentTimeMillis()
    val random = (1000..9999).random()
    return "${prefix}_${timestamp}_${random}"
}

// API 키 생성
fun generateApiKey(): String {
    val secureRandom = SecureRandom()
    val bytes = ByteArray(32)
    secureRandom.nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}

// 카카오 토큰 생성 (모킹용)
fun generateKakaoToken(): String {
    val secureRandom = SecureRandom()
    val bytes = ByteArray(40)
    secureRandom.nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}

// 날짜 포맷터
object DateTimeUtil {
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun parseDate(dateStr: String): LocalDateTime {
        return LocalDateTime.parse("${dateStr}T00:00:00")
    }

    fun formatDate(dateTime: LocalDateTime): String {
        return dateTime.format(DATE_FORMATTER)
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(DATETIME_FORMATTER)
    }
}

// 카카오 계정 ID 생성 (모킹용)
fun generateKakaoAccountId(): String {
    val timestamp = System.currentTimeMillis()
    return "kakao_user_$timestamp"
}