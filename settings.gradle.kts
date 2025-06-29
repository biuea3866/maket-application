rootProject.name = "OpenMarketApplication"

pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings
    val kotlinJvmPluginVersion: String by settings
    val springDependencyManagementVersion: String by settings

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    plugins {
        kotlin("jvm") version kotlinJvmPluginVersion
        id("org.jetbrains.kotlin.kapt") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
    }
}

include(
    ":user",
    ":order",
    ":shipping",
    ":store",
    ":catalog",
    ":naver-mocking-api",
    ":coupang-mocking-api",
    ":carrot-mocking-api",
    ":gmarket-mocking-api",
    ":kakao-mocking-api",
)
project(":user").projectDir = file("user")
project(":order").projectDir = file("order")
project(":shipping").projectDir = file("shipping")
project(":store").projectDir = file("store")
project(":catalog").projectDir = file("catalog")
project(":naver-mocking-api").projectDir = file("naver-mocking-api")
project(":coupang-mocking-api").projectDir = file("coupang-mocking-api")
project(":carrot-mocking-api").projectDir = file("carrot-mocking-api")
project(":gmarket-mocking-api").projectDir = file("gmarket-mocking-api")
project(":kakao-mocking-api").projectDir = file("kakao-mocking-api")