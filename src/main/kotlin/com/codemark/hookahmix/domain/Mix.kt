package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.*
import javax.persistence.*;


@Entity
@Table(name = "mixes")
class Mix {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var mixesId : Long = 0;
    var title : String = "";
    var rating : Int = 0;
    var tags : String = "";
    var description: String = "";
    var strength: Int = 5
    var mixUrl: String = "";

    @ManyToMany
    @JoinTable(
            name = "components",
            joinColumns = [JoinColumn(name = "mix_id")],
            inverseJoinColumns = [JoinColumn(name = "tobacco_id")]
    )
    var tobaccoMixList: MutableList<Tobacco> = mutableListOf();

    @JsonIgnore
    @OneToMany(mappedBy = "mix")
    var components: MutableSet<Component> = mutableSetOf()

    @Transient
    var status: MixSet = MixSet.MATCH_BAR

    override fun toString(): String {
        return "Mix $title";
    }


}
