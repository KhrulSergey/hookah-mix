package com.codemark.hookahmix.domain.dto

data class MixDto (var title : String = "Default Mix",
                   var tags : String = "Hot, Spicy") {

    override fun toString(): String {
        return "Mix '$title': $tags"
    }
}