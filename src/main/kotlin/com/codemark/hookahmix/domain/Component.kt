package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonValue
import jdk.nashorn.internal.ir.annotations.Reference
import javax.persistence.*;

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

    @JsonValue
    var composition: Int = 0

}