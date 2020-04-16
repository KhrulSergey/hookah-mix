package com.codemark.hookahmix.domain

import javax.persistence.*

/** Модель для хранения вкуса табака */
@Entity
@Table(name = "tobacco_tastes")
class TobaccoTastes(taste: Taste?, tobacco: Tobacco?) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tobacco_taste_id")
    var tobaccoTasteId: Long? = null

    @ManyToOne
    @JoinColumn(name = "tastes_id", referencedColumnName = "tastes_id")
    var taste: Taste? = taste

    @ManyToOne
    @JoinColumn(name = "tobaccos_id", referencedColumnName = "tobaccos_id")
    var tobacco: Tobacco? = tobacco

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "tobaccoTasteId = $tobaccoTasteId " +
                    ")"
}