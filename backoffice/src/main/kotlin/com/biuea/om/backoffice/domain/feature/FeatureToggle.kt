package com.biuea.om.backoffice.domain.feature

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update feature_toggle set deleted_at = now() where id = ?")
@Table(name = "feature_toggle")
@Entity
class FeatureToggle(
    @Column(name = "name")
    val name: String,
    @Column(name = "toggle")
    val toggle: Boolean,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    companion object {
        fun create(
            name: String,
            toggle: Boolean
        ): FeatureToggle {
            return FeatureToggle(
                name = name,
                toggle = toggle
            )
        }
    }
}