package com.biuea.om.store.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update store_integration set deleted_at = now() where id = ?")
@Table(name = "store_integration")
@Entity
class StoreIntegration(
    @Column(name = "integration_platform", columnDefinition = "varchar(30)")
    @Enumerated(EnumType.STRING)
    val integrationPlatform: IntegrationPlatform,

    @Column(name = "platform_id", columnDefinition = "text")
    val platformId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "store_id",
        columnDefinition = "bigint(20)",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val store: Store,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

enum class IntegrationPlatform {
    KAKAO,
    NAVER,
    COUPANG,
    GMARKET,
    CARROT,
    OPEN_MARKET
}