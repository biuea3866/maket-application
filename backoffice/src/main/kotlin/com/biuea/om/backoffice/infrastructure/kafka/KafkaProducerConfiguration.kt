package com.biuea.om.backoffice.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
) {
    @Bean
    fun<T> producerFactory(): ProducerFactory<String, Any> {
        val config = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to this.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaSerializer<T>()::class.java,
        )

        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun <T> kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory<T>())
    }
}

class KafkaSerializer<T> : Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())

        return objectMapper.writeValueAsBytes(data)
    }
}