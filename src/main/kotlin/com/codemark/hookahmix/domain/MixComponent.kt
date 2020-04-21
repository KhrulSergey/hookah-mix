package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
@Table(name = "components")
class MixComponent(composition: Int? = 0, tobacco: Tobacco? = null) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("componentId")
    @Column(name = "components_id")
    var id: Long = 0;

    @Column(name = "composition")
    var composition: Int? = composition;

    @ManyToOne
    @JoinColumn(name = "tobacco_id")
    var tobaccoRef: Tobacco? = tobacco;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mix_id")
    var mixRef: Mix? = null;

    //Список табаков-замен из тех табаков что есть у пользователя в баре
    @Transient
    var tobaccoReplacements: MutableList<Tobacco> = mutableListOf()

    override fun toString(): String {
        val compositionStr: String = if (composition != 0) " - $composition%" else "";
        val makerStr: String = if (tobaccoRef?.maker != null) "${tobaccoRef?.maker?.title}: " else "";
        return "Id: $id ->$makerStr: ${tobaccoRef?.title} - $compositionStr";
    }

}