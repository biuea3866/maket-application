package com.biuea.om.store.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.ZonedDateTime

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update product set deleted_at = now() where id = ?")
@Table(name = "product")
@Entity
class Product(
    @Column(name = "name", columnDefinition = "varchar(100)")
    val name: String,
    @Column(name = "description", columnDefinition = "text")
    val description: String,
    @Column(name = "quantity", columnDefinition = "int(11)")
    val quantity: Int,
    @Column(name = "price", columnDefinition = "float")
    val price: Double,
    @Column(name = "status", columnDefinition = "varchar(30)")
    @Enumerated(EnumType.STRING)
    val status: ProductStatus,
    @Column(name = "registered_at", columnDefinition = "timestamp")
    val registeredAt: ZonedDateTime,
    @Column(name = "confirmed_at", columnDefinition = "timestamp")
    val confirmedAt: ZonedDateTime?,
    @Column(name = "updated_at", columnDefinition = "timestamp")
    val updatedAt: ZonedDateTime,
    @Column(name = "updated_by", columnDefinition = "bigint")
    val updatedBy: Long,
    @Column(name = "deleted_at", columnDefinition = "timestamp")
    val deletedAt: ZonedDateTime?,
    @Column(name = "deleted_by", columnDefinition = "bigint")
    val deletedBy: Long?,
    @Column(name = "registered_by", columnDefinition = "bigint")
    val registeredBy: Long,
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        mappedBy = "product",
        orphanRemoval = true
    )
    val registerInformations: List<ProductRegisterInformation>,
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        mappedBy = "product",
        orphanRemoval = true
    )
    val productNotConfirmedReasons: List<ProductNotConfirmedReason>,
    @Column(name = "store_id", columnDefinition = "bigint(20)")
    val storeId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

enum class ProductStatus {
    REGISTER,
    CONFIRM,
    REVIEW,
    NOT_CONFIRM
}