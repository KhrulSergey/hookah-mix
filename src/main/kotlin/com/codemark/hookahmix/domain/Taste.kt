package com.codemark.hookahmix.domain

import javax.persistence.*

@Entity
@Table(name = "tastes")
class Taste (title: String = "") {

    @Id
    @Column(name = "tastes_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long = 0;

    @Column(name = "taste")
    var title : String = title;

    override fun toString(): String {
        return title;
    }

}