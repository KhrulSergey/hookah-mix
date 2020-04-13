package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class MixSet(@JsonProperty("title") val title: String) {
    NULL_VALUE("Неизвестен"),
    MATCH_BAR("Все есть"),
    REPLACEMENT_BAR("С заменой"),
    PARTIAL_BAR("Нужно докупить");

//    @JsonProperty("id")
    @JsonValue
    fun getId(): String {
        return name
    }

}
