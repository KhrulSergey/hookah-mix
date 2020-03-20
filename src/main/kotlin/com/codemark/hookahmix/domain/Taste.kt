package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence.*;

@Entity
@Table(name = "tastes")
class Taste {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var tastesId : Long = 0;
    var taste : String = "";

    override fun toString(): String {
        return taste;
    }

}