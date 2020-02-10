package com.codemark.hookahmix.domain


/**
 * class for test of method adding new item;
 * when designing of logic basic class will be enabled
 */

//data class Tobacco(@Id var id : Long = 0,
//                   @Column var title : String = "",
//                   @Column var maker : String = "",
//                   @Column var tag : String = "",
//                   @Column var strength : Int = 5,
//                   @Column var rating : Int = 0) {
//
//
//    override fun toString(): String = "Tobacco $title by $maker"
//}
data class Tobacco(var id : Long = 0,
                   var title : String = "",
                   var maker : String = "") {


    override fun toString(): String = "Tobacco $title by $maker"
}