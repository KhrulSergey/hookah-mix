package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class MixSet(@JsonProperty("title") val title: String) {
    MATCH_BAR("Все есть"),
    REPLACEMENT_BAR("С заменой"),
    PARTIAL_BAR("Нужно докупить");

    @JsonProperty("id")
    fun getId(): String {
        return name
    }
}
