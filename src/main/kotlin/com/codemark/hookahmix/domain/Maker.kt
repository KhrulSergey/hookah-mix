package com.codemark.hookahmix.domain

import javax.persistence.*;

@Entity
@Table(name = "makers")
class Maker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var makersId : Long = 0;
    var title : String = "";
    var image : String = "";
    @Column(name = "founding_date")
    var foundingDate : String = "";
    var description : String = "";
    @Column(name = "price_range")
    var priceRange : String = "";

    @OneToMany
    @JoinColumn(name = "tobaccos_id")
    var tobaccos : Set<Tobacco>? = null;

    override fun toString(): String {
        return title;
    }

}