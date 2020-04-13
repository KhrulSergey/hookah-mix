package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@Entity
@Table(name = "mixes")
class Mix {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var mixesId: Long = 0;

    @Column(name = "title")
    var title: String = "";

    @Column(name = "rating")
    var rating: Int = 0;

    @Column(name = "tags")
    var tags: String = "";

    @Column(name = "description")
    var description: String = "";

    @Column(name = "strength")
    var strength: Int = 0

    @JsonIgnore
    @OneToMany(mappedBy = "mix")
    var components: MutableSet<Component> = mutableSetOf()

    @ManyToMany
    @JoinTable(
            name = "components",
            joinColumns = [JoinColumn(name = "mix_id")],
            inverseJoinColumns = [JoinColumn(name = "tobacco_id")]
    )
    var tobaccoMixList: MutableList<Tobacco> = mutableListOf();

    @Transient
    var sourceUrl: String = "";

    @Transient
    var status: MixSet = MixSet.NULL_VALUE



    override fun toString(): String {
        return "Mix $title";
    }


}
