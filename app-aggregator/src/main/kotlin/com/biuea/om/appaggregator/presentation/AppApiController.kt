package com.biuea.om.appaggregator.presentation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/app"])
class AppApiController {
    @GetMapping(value = ["/test"])
    fun test(
        @RequestHeader("X-User-Id") userId: Long,
        @RequestHeader("X-Feature-Toggles") featureToggles: List<String>,
    ) {
        println(userId)
        println(featureToggles)
    }
}