package com.biuea.om.store.presentation.consumer

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.converter.JsonMessageConverter
import org.springframework.retry.backoff.BackOffPolicy
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
class KafkaConsumerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
) {
    @Bean
    fun <T> kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val config = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to this.bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.qualifiedName,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.qualifiedName,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
        )

        return ConcurrentKafkaListenerContainerFactory<String, Any>()
            .apply {
                this.setRecordMessageConverter(JsonMessageConverter())
                this.consumerFactory = DefaultKafkaConsumerFactory<String, Any>(config)
                this.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
            }
    }

    @Bean
    fun retryTemplate(): RetryTemplate {
        val backOffPolicy = FixedBackOffPolicy().apply { backOffPeriod = 1000L }
        val retryPolicy = SimpleRetryPolicy().apply { maxAttempts = 3 }

        return RetryTemplate()
            .apply {
                setBackOffPolicy(backOffPolicy)
                setRetryPolicy(retryPolicy)
            }
    }
}

