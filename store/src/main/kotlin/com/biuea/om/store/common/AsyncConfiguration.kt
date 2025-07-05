package com.biuea.om.store.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfiguration {
    @Bean("taskExecutor")
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
            .apply {
                corePoolSize = 2
                maxPoolSize = 2
                queueCapacity = QUEUE_CAPACITY
            }

        executor.initialize()

        return executor
    }

    companion object {
        const val QUEUE_CAPACITY = 500
    }
}