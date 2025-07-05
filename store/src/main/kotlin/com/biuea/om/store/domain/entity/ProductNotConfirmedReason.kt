package com.biuea.om.store.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.ZonedDateTime

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update product_not_confirmed_reason set deleted_at = now() where id = ?")
@Table(name = "product_not_confirmed_reason")
@Entity
class ProductNotConfirmedReason(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "product_id",
        columnDefinition = "bigint(20)",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val product: Product,

    @Column(name = "reason", columnDefinition = "text")
    val reason: String?,

    @Column(name = "deleted_at")
    val deletedAt: ZonedDateTime?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}