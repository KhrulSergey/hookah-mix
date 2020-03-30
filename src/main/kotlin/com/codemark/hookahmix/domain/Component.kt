package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.persistence.*

@Entity
@Table(name = "components")
class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "components_id")
    var componentsId: Long = 0;

    @ManyToOne
    @JoinColumn(name = "tobacco_id")
    var tobacco: Tobacco? = null

    @ManyToOne
    @JoinColumn(name = "mix_id")
    var mix: Mix? = null

//    @JsonProperty("composition")
//    @JsonValue
    var composition: Int = 0

    @JsonProperty("composition")
    @JsonValue
    override fun toString(): String {
        return "Id: " + componentsId + ", mix id: " + mix!!.mixesId + ", tobacco id: " +
                tobacco!!.tobaccosId + ", composition: " + composition
    }

}