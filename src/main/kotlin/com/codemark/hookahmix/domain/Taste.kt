package com.codemark.hookahmix.domain

import javax.persistence.*

@Entity
@Table(name = "tastes")
class Taste {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var tastesId : Long = 0;
    //TODO изменить наименование поля
    var taste : String = "";

    override fun toString(): String {
        return taste;
    }

}