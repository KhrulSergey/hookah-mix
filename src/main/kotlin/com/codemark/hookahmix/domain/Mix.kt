package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonValue
import javax.persistence.*;


@Entity
@Table(name = "mixes")
class Mix {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var mixesId : Long = 0;
    var title : String = "";
    var rating : String = "";
    var tags : String = "";
    var description: String = "";
    var strength: Int = 5

    @ManyToMany
    @JoinTable(
            name = "components",
            joinColumns = [JoinColumn(name = "mix_id")],
            inverseJoinColumns = [JoinColumn(name = "tobacco_id")]
    )
    var tobaccoMixList: MutableList<Tobacco> = mutableListOf();

    @Transient
    var status: MixSet = MixSet.MATCH_BAR

    /*
    TODO add relations
     */

    override fun toString(): String {
        return "Mix $title ";
    }
}
