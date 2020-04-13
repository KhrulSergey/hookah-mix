package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "components")
class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "components_id")
    var componentsId: Long = 0;

    @Column(name = "composition")
    var composition: Int = 0;

    @ManyToOne
    @JoinColumn(name = "tobacco_id")
    var tobacco: Tobacco? = null
        get() {
            field?.composition = this.composition;
            return field;
        };

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mix_id")
    var mix: Mix? = null;

    override fun toString(): String {
        return "Id: " + componentsId + ", mix id: " + mix!!.mixesId + ", tobacco id: " +
                tobacco!!.id + ", composition: " + composition;
    }

}