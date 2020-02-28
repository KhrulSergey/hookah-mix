package com.codemark.hookahmix.domain

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

    var set: MixSet = MixSet.MATCH_BAR
    /*
    TODO add relations
     */

    override fun toString(): String {
        return "Mix $title ";
    }
}