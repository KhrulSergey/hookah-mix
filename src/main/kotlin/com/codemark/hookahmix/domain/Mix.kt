package com.codemark.hookahmix.domain


data class Mix (var id : Long = 0,
                var title : String = "") {

    override fun toString(): String {
        return "Mix $title ";
    }
}