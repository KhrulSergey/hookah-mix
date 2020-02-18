package com.codemark.hookahmix.domain


import javax.persistence.*;

@Entity
@Table(name = "tobaccos")
data class Tobacco(
        var title : String,
//                   var makerId : Int,
        var description : String,
//                   var tasteId : Int,
        var strength : Int = 5,
        var image : String = "",
        var tags : String = "") {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tobaccos_id")
    var tobaccosId : Long = 0;

    @ManyToOne
    @JoinColumn(name = "maker_id")
    var maker : Maker? = null;
    @OneToOne
    @JoinColumn(name = "taste_id")
    var taste : Taste? = null;
    var status: TobaccoStatus = TobaccoStatus.NEED_BAR

    override fun toString(): String = "Tobacco $title: $tags";
}
