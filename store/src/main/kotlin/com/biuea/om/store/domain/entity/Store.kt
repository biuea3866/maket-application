package com.biuea.om.store.domain.entity

import com.biuea.om.store.domain.value.StoreRegistrationInfo
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.ZonedDateTime

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update store set deleted_at = now() where id = ?")
@Table(name = "store")
@Entity
class Store(
    @Column(name = "name", columnDefinition = "varchar(200)")
    val name: String,

    @Column(name = "description", columnDefinition = "text")
    val description: String?,

    @Column(name = "user_id", columnDefinition = "bigint(20)")
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(30)")
    val status: StoreStatus,

    @Column(name = "registered_at", columnDefinition = "timestamp")
    val registeredAt: ZonedDateTime,
    @Column(name = "confirmed_at", columnDefinition = "timestamp")
    val confirmedAt: ZonedDateTime?,
    @Column(name = "updated_at", columnDefinition = "timestamp")
    val updatedAt: ZonedDateTime,
    @Column(name = "deleted_at", columnDefinition = "timestamp")
    val deletedAt: ZonedDateTime?,
    @Column(name = "registered_by", columnDefinition = "bigint(20)")
    val registeredBy: Long,
    @Column(name = "deleted_by", columnDefinition = "bigint(20)")
    val deletedBy: Long?,

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "store",
        orphanRemoval = true,
        cascade = [CascadeType.ALL]
    )
    val histories: MutableList<StoreHistory> = mutableListOf(),

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "store",
        orphanRemoval = true,
        cascade = [CascadeType.ALL]
    )
    val integrations: MutableList<StoreIntegration> = mutableListOf(),

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "store",
        orphanRemoval = true,
        cascade = [CascadeType.ALL]
    )
    val notConfirmedReasons: MutableList<StoreNotConfirmedReason> = mutableListOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    fun addStoreIntegration(registrationInfo: StoreRegistrationInfo) {
        val integration = StoreIntegration.create(
            registrationInfo = registrationInfo,
            store = this
        )
        this.integrations.add(integration)
    }

    companion object {
        fun register(
            name: String,
            description: String?,
            userId: Long,
            registrationInfo: StoreRegistrationInfo
        ): Store {
            return Store(
                name = name,
                description = description,
                userId = userId,
                status = registrationInfo.integrationStatus,
                registeredAt = ZonedDateTime.now(),
                confirmedAt = null,
                updatedAt = ZonedDateTime.now(),
                deletedAt = null,
                registeredBy = userId,
                deletedBy = null,
                histories = mutableListOf(),
                integrations = mutableListOf(),
                notConfirmedReasons = mutableListOf(),
            )
        }
    }
}

enum class StoreStatus {
    REGISTER, // naver: PENDING, SUSPENDED
    CONFIRM,  // naver: APPROVED
    NOT_CONFIRM // naver: REJECTED
}