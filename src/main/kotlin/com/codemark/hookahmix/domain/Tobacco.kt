package com.codemark.hookahmix.domain


import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*;

@Entity
@Table(name = "tobaccos")
data class Tobacco(
        var title : String,
        var description : String,
        var strength : Int = 5,
        var image : String = "",
        var tags : String = "") {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tobaccos_id")
    var tobaccosId : Long = 0;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "maker_id")
    var maker : Maker? = null;
    @OneToOne
    @JoinColumn(name = "taste_id")
    var taste : Taste? = null;

    @Transient
    @JsonIgnore
    var existInBar : Boolean = false;


    override fun toString(): String = "Tobacco $title: $tags by $maker";
}