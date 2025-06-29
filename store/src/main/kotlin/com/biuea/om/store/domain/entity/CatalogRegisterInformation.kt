package com.biuea.om.store.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.ZonedDateTime

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update catalog_register_information set deleted_at = now() where id = ?")
@Table(name = "catalog_register_information")
@Entity
class CatalogRegisterInformation(
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(30)")
    val status: RegisterPlatform,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "catalog_id",
        columnDefinition = "bigint(20)",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val catalog: Catalog,

    @Column(name = "deleted_at")
    val deletedAt: ZonedDateTime?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

enum class RegisterPlatform {
    KAKAO,
    NAVER,
    COUPANG,
    GMARKET,
    CARROT,
    OPEN_MARKET
}